package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mg.emberframework.annotation.http.Controller;
import mg.emberframework.annotation.http.Get;
import mg.emberframework.annotation.http.Post;
import mg.emberframework.annotation.http.RequestParameter;
import mg.emberframework.annotation.http.Url;
import mg.emberframework.core.data.File;
import mg.emberframework.core.data.ModelView;
import mg.emberframework.core.data.Session;
import model.Database;
import model.PrixVol;
import model.Utilisateur;
import model.Ville;
import model.Vol;
import model.VolSiege;

@Controller
public class VolFrontController {
    Session session;
    
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Get
    @Url("/user-vol")
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
            System.out.println("Connexion a la base de donnees etablie");

            villes = Ville.getAll(conn);
            if (villes == null) {
                villes = new ArrayList<>();
                mv.addObject("errorMessage", "Impossible de charger la liste des villes.");
            }
            System.out.println("Villes chargees : " + villes.size());

            boolean isSearchEmpty = 
                (idVilleDepart == null || idVilleDepart == 0) &&
                (idVilleArrivee == null || idVilleArrivee == 0) &&
                (dateDepart == null || dateDepart.trim().isEmpty()) &&
                (dateArrivee == null || dateArrivee.trim().isEmpty()) &&
                (prixMin == null || prixMin.trim().isEmpty()) &&
                (prixMax == null || prixMax.trim().isEmpty());

            if (isSearchEmpty) {
                System.out.println("Aucun critere de recherche, chargement de tous les vols...");
                vols = Vol.getAll(conn);
            } else {
                System.out.println("Recherche avec criteres : villeDepart=" + idVilleDepart + ", villeArrivee=" + idVilleArrivee);

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

                vols = Vol.search(conn, villeDepartId, villeArriveeId, dateDepart, dateArrivee, minPrix, maxPrix);
                System.out.println("Recherche terminee : " + vols.size() + " vols trouves");
            }

            if (vols == null) {
                vols = new ArrayList<>();
                mv.addObject("errorMessage", "Aucun vol trouve ou erreur lors de la recuperation des vols.");
            }

        } catch (SQLException e) {
            mv.addObject("errorMessage", "Erreur SQL lors de la recuperation des donnees : " + e.getMessage());
            mv.setUrl("/frontoffice/error.jsp"); // Redirect to error.jsp
            e.printStackTrace();
            return mv;
        } catch (Exception e) {
            mv.addObject("errorMessage", "Erreur inattendue : " + e.getClass().getName() + " - " + e.getMessage());
            mv.setUrl("/frontoffice/error.jsp"); // Redirect to error.jsp
            e.printStackTrace();
            return mv;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Connexion fermee");
                } catch (SQLException e) {
                    mv.addObject("errorMessage", "Erreur lors de la fermeture de la connexion : " + e.getMessage());
                    mv.setUrl("/frontoffice/error.jsp"); // Redirect to error.jsp
                    e.printStackTrace();
                    return mv;
                }
            }
        }

        mv.addObject("vols", vols);
        mv.addObject("villes", villes);
        mv.setUrl("/frontoffice/listeVol.jsp");

        return mv;
    }


    @Get
    @Url("/user-vol/details")
    public ModelView showVolDetails(@RequestParameter("volId") Integer volId) throws Exception {
        Connection conn = null;
        ModelView mv = new ModelView();

        try {
            conn = Database.getConnection();
            Vol vol = Vol.getById(conn, volId);
            List<VolSiege> volSieges = VolSiege.getByVolId(conn, volId);
            List<PrixVol> prixVols = PrixVol.getByVolId(conn, volId);

            if (vol == null) {
                mv.addObject("errorMessage", "Vol avec l'ID " + volId + " non trouvé.");
                mv.setUrl("/frontoffice/error.jsp");
            } else {
                mv.addObject("vol", vol);
                mv.addObject("volSieges", volSieges);
                mv.addObject("prixVols", prixVols);
                mv.setUrl("/frontoffice/volDetails.jsp");
            }

        } catch (Exception e) {
            mv.addObject("errorMessage", "Erreur lors du chargement des détails du vol: " + e.getMessage());
            mv.setUrl("/frontoffice/error.jsp");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    mv.addObject("errorMessage", "Erreur lors de la fermeture de la connexion: " + e.getMessage());
                    mv.setUrl("/frontoffice/error.jsp");
                    e.printStackTrace();
                }
            }
        }

        return mv;
    }


}
