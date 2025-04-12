package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

import mg.emberframework.annotation.http.Controller;
import mg.emberframework.annotation.http.Get;
import mg.emberframework.annotation.http.Post;
import mg.emberframework.annotation.http.RequestParameter;
import mg.emberframework.annotation.http.Url;
import mg.emberframework.core.data.ModelView;
import model.Avion;
import model.Database;
import model.PrixVol;
import model.TypeSiege;
import model.Ville;
import model.Vol;
import model.VolSiege;

@Controller
public class VolController {
    
    @Get
    @Url("/vol/insert")
    public ModelView formulaireVol() throws Exception{
        Connection conn= Database.getConnection();

        ModelView mv = new ModelView();
        List<Avion> avions = Avion.getAll(conn);
        List<Ville> villes = Ville.getAll(conn);
        List<TypeSiege> types= TypeSiege.getAll(conn);

        mv.addObject("villes", villes);
        mv.addObject("avions", avions);
        mv.addObject("types", types);
        
        mv.setUrl("/backoffice/insertVol.jsp");
        return mv;
    }

    @Post
    @Url("/vol/insert")
    public ModelView insertVol(
            @RequestParameter("avion") Integer avionId,
            @RequestParameter("departureVille") Integer departureVilleId,
            @RequestParameter("destinationVille") Integer destinationVilleId,
            @RequestParameter("departureTime") String departureTime,
            @RequestParameter("arrivalTime") String arrivalTime,
            @RequestParameter("siegeData") String siegeData
    ) throws Exception {
        ModelView mv = new ModelView();
        Connection conn = null;
        boolean success = false;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            Avion avion = Avion.getById(conn, avionId);
            Ville departureVille = Ville.getById(conn, departureVilleId);
            Ville destinationVille = Ville.getById(conn, destinationVilleId);

            Timestamp departureTimestamp = Timestamp.valueOf(departureTime.replace("T", " ") + ":00");
            Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalTime.replace("T", " ") + ":00");

            Vol vol = new Vol();
            vol.setAvion(avion);
            vol.setVilleDepart(departureVille);
            vol.setVilleArrivee(destinationVille);
            vol.setDepart(departureTimestamp);
            vol.setArrivee(arrivalTimestamp);
            int volId = vol.insert(conn);

            // Parser la chaîne siegeData (ex: "1:10.5:50,2:20.0:30")
            String[] siegeEntries = siegeData.split(",");
            for (String entry : siegeEntries) {
                String[] parts = entry.split(":");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Format invalide pour siegeData: " + entry);
                }
                int typeId = Integer.parseInt(parts[0]);
                double prix = Double.parseDouble(parts[1]);
                int nombre = Integer.parseInt(parts[2]);

                TypeSiege typeSiege = TypeSiege.getById(conn, typeId);

                PrixVol prixVol = new PrixVol();
                prixVol.setVol(new Vol(volId, avion, departureVille, destinationVille, departureTimestamp, arrivalTimestamp));
                prixVol.setTypeSiege(typeSiege);
                prixVol.setPrix(prix);
                prixVol.insert(conn);

                VolSiege volSiege = new VolSiege();
                volSiege.setVol(new Vol(volId, avion, departureVille, destinationVille, departureTimestamp, arrivalTimestamp));
                volSiege.setTypeSiege(typeSiege);
                volSiege.setNombre(nombre);
                volSiege.insert(conn);
            }

            conn.commit();
            success = true;

            mv.setRedirect(true);
            mv.setUrl("../vol");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            mv.addObject("errorMessage", "Erreur lors de l'insertion du vol : " + e.getMessage());
            mv.setUrl("/backoffice/insertVol.jsp");
            e.printStackTrace();

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return mv;
    }

    @Get
    @Url("/vol")
    public ModelView flightList(
            @RequestParameter("villeDepart") Integer idVilleDepart,
            @RequestParameter("villeArrivee") Integer idVilleArrivee,
            @RequestParameter("dateDepart") String dateDepart,
            @RequestParameter("dateArrivee") String dateArrivee,
            @RequestParameter("prixMin") String prixMin,
            @RequestParameter("prixMax") String prixMax
    ) throws Exception {
        Connection conn = null;
        ModelView mv = new ModelView();
        List<Vol> vols = new ArrayList<>();
        List<Ville> villes = new ArrayList<>();

        try {
            conn = Database.getConnection();
            System.out.println("Connexion à la base de données établie");

            // Chargement des villes
            villes = Ville.getAll(conn);
            if (villes == null) {
                villes = new ArrayList<>();
                mv.addObject("errorMessage", "Impossible de charger la liste des villes.");
            }
            System.out.println("Villes chargées : " + villes.size());

            // Vérifie si tous les champs sont vides
            boolean isSearchEmpty = 
                (idVilleDepart == null || idVilleDepart == 0) &&
                (idVilleArrivee == null || idVilleArrivee == 0) &&
                (dateDepart == null || dateDepart.trim().isEmpty()) &&
                (dateArrivee == null || dateArrivee.trim().isEmpty()) &&
                (prixMin == null || prixMin.trim().isEmpty()) &&
                (prixMax == null || prixMax.trim().isEmpty());

            if (isSearchEmpty) {
                System.out.println("Aucun critère de recherche, chargement de tous les vols...");
                vols = Vol.getAll(conn);
            } else {
                System.out.println("Recherche avec critères : villeDepart=" + idVilleDepart + ", villeArrivee=" + idVilleArrivee);

                // Conversion des paramètres
                int villeDepartId = idVilleDepart != null ? idVilleDepart : 0;
                int villeArriveeId = idVilleArrivee != null ? idVilleArrivee : 0;

                double minPrix = 0.0;
                if (prixMin != null && !prixMin.trim().isEmpty()) {
                    try {
                        minPrix = Double.parseDouble(prixMin);
                    } catch (NumberFormatException e) {
                        System.out.println("Prix minimum non valide : " + prixMin);
                    }
                }

                double maxPrix = 0.0;
                if (prixMax != null && !prixMax.trim().isEmpty()) {
                    try {
                        maxPrix = Double.parseDouble(prixMax);
                    } catch (NumberFormatException e) {
                        System.out.println("Prix maximum non valide : " + prixMax);
                    }
                }

                // Pas de validation supplémentaire des dates ici, déléguer à Vol.search
                vols = Vol.search(conn, villeDepartId, villeArriveeId, dateDepart, dateArrivee, minPrix, maxPrix);
                System.out.println("Recherche terminée : " + vols.size() + " vols trouvés");
            }

            if (vols == null) {
                vols = new ArrayList<>();
                mv.addObject("errorMessage", "Aucun vol trouvé ou erreur lors de la récupération des vols.");
            }

        } catch (SQLException e) {
            mv.addObject("errorMessage", "Erreur SQL lors de la récupération des données : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mv.addObject("errorMessage", "Erreur inattendue : " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Connexion fermée");
                } catch (SQLException e) {
                    mv.addObject("errorMessage", "Erreur lors de la fermeture de la connexion : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        mv.addObject("vols", vols);
        mv.addObject("villes", villes);
        mv.setUrl("/backoffice/listeVol.jsp");

        return mv;
    }



}
