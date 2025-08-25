package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mg.emberframework.annotation.http.Controller;
import mg.emberframework.annotation.http.Get;
import mg.emberframework.annotation.http.Post;
import mg.emberframework.annotation.http.RequestParameter;
import mg.emberframework.annotation.http.Url;
import mg.emberframework.core.data.File;
import mg.emberframework.core.data.ModelView;
import mg.emberframework.core.data.Session;
import model.Database;
import model.Paiement;
import model.PrixVol;
import model.Reservation;
import model.ReservationConfig;
import model.TypeSiege;
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
            @RequestParameter("prixMax") String prixMax) throws Exception {
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

            boolean isSearchEmpty = (idVilleDepart == null || idVilleDepart == 0) &&
                    (idVilleArrivee == null || idVilleArrivee == 0) &&
                    (dateDepart == null || dateDepart.trim().isEmpty()) &&
                    (dateArrivee == null || dateArrivee.trim().isEmpty()) &&
                    (prixMin == null || prixMin.trim().isEmpty()) &&
                    (prixMax == null || prixMax.trim().isEmpty());

            if (isSearchEmpty) {
                System.out.println("Aucun critere de recherche, chargement de tous les vols...");
                vols = Vol.getAll(conn);
            } else {
                System.out.println(
                        "Recherche avec criteres : villeDepart=" + idVilleDepart + ", villeArrivee=" + idVilleArrivee);

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
            if (vol == null) {
                mv.addObject("errorMessage", "Vol avec l'ID " + volId + " non trouvé.");
                mv.setUrl("/frontoffice/error.jsp");
                return mv;
            }
            List<VolSiege> volSieges = VolSiege.getByVolId(conn, volId);
            List<PrixVol> prixVols = PrixVol.getByVolId(conn, volId);
            mv.addObject("vol", vol);
            mv.addObject("volSieges", volSieges != null ? volSieges : new ArrayList<VolSiege>());
            mv.addObject("prixVols", prixVols != null ? prixVols : new ArrayList<PrixVol>());
            mv.setUrl("/frontoffice/volDetails.jsp");
        } catch (SQLException e) {
            mv.addObject("errorMessage", "Erreur SQL lors du chargement des détails du vol: " + e.getMessage());
            mv.setUrl("/frontoffice/error.jsp");
            e.printStackTrace();
        } catch (Exception e) {
            mv.addObject("errorMessage", "Erreur inattendue lors du chargement des détails du vol: " + e.getMessage());
            mv.setUrl("/frontoffice/error.jsp");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    mv.addObject("errorMessage", "Erreur lors de la fermeture de la connexion: " + e.getMessage());
                    mv.setUrl("/frontoffice/error.jsp");
                    e.printStackTrace();
                }
            }
        }
        return mv;
    }
    
@Post
@Url("/user-vol/reserve")
public ModelView reserve(
        @RequestParameter("volId") Integer volId,
        @RequestParameter("utilisateurId") Integer utilisateurId,
        @RequestParameter("allParams") String allParams,
        @RequestParameter("passeport") File passeport) throws Exception {
    Connection conn = null;
    ModelView mv = new ModelView();

    try {
        conn = Database.getConnection();
        conn.setAutoCommit(false);

        Vol vol = Vol.getById(conn, volId);
        if (vol == null) {
            throw new IllegalArgumentException("Vol avec l'ID " + volId + " non trouvé.");
        }

        Utilisateur utilisateur = Utilisateur.getById(conn, utilisateurId);
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur avec l'ID " + utilisateurId + " non trouvé.");
        }

        // Vérification de l'heure de réservation
        ReservationConfig config = ReservationConfig.getLatest(conn);
        if (config != null) {
            long currentTime = System.currentTimeMillis();
            long volDepartureTime = vol.getDepart().getTime();
            long hoursBeforeFlight = (volDepartureTime - currentTime) / (1000 * 60 * 60); // Conversion en heures

            if (hoursBeforeFlight < config.getHeureReservation()) {
                mv.addObject("errorMessage", 
                    "La réservation ne peut pas être effectuée. Il faut réserver au moins " + 
                    config.getHeureReservation() + " heures avant le départ du vol.");
                mv.addObject("vol", vol);
                mv.addObject("volSieges", VolSiege.getByVolId(conn, volId));
                mv.addObject("prixVols", PrixVol.getByVolId(conn, volId));
                mv.setUrl("/frontoffice/volDetails.jsp?volId=" + volId);
                return mv;
            }
        }

        // Vérification de l'image du passeport
        if (passeport == null || passeport.getFileBytes() == null || passeport.getFileBytes().length == 0) {
            mv.addObject("errorMessage", "Le fichier contenant le passeport n'a pas ete televersee");
            mv.addObject("vol", vol);
            mv.addObject("volSieges", VolSiege.getByVolId(conn, volId));
            mv.addObject("prixVols", PrixVol.getByVolId(conn, volId));
            mv.setUrl("/frontoffice/volDetails.jsp?volId=" + volId);
            return mv;
        }

        // Vérifier l'extension du fichier pour s'assurer qu'il s'agit d'une image
        String fileName = passeport.getFileName();
        if (fileName == null || !(fileName.toLowerCase().endsWith(".png") || 
                                  fileName.toLowerCase().endsWith(".jpg") || 
                                  fileName.toLowerCase().endsWith(".jpeg"))) {
            throw new IllegalArgumentException("Le fichier téléversé doit être une image (PNG, JPG, JPEG).");
        }

        // Vérifier la taille du fichier (max 2 Mo)
        long maxSize = 2 * 1024 * 1024; // 2 Mo
        if (passeport.getFileBytes().length > maxSize) {
            mv.addObject("errorMessage", "Veuillez choisir une image inferieure a 2Mo");
            mv.addObject("vol", vol);
            mv.addObject("volSieges", VolSiege.getByVolId(conn, volId));
            mv.addObject("prixVols", PrixVol.getByVolId(conn, volId));
            mv.setUrl("/frontoffice/volDetails.jsp?volId=" + volId);
            return mv;
        }

        Reservation reservation = new Reservation();
        reservation.setVol(vol);
        reservation.setUtilisateur(utilisateur);
        reservation.setDate(new Timestamp(System.currentTimeMillis()));
        reservation.setPasseport(passeport.getFileBytes());

        List<VolSiege> volSieges = VolSiege.getByVolId(conn, volId);
        if (allParams != null && !allParams.isEmpty()) {
            String[] paramPairs = allParams.split(",");
            for (String pair : paramPairs) {
                String[] parts = pair.split(":");
                if (parts.length == 2) {
                    try {
                        int typeSiegeId = Integer.parseInt(parts[0].trim());
                        int nombre = Integer.parseInt(parts[1].trim());
                        if (nombre > 0) {
                            VolSiege volSiege = volSieges.stream()
                                    .filter(vs -> vs.getTypeSiege().getId() == typeSiegeId)
                                    .findFirst()
                                    .orElse(null);
                            if (volSiege == null) {
                                throw new IllegalArgumentException(
                                        "Type de siège ID " + typeSiegeId + " non trouvé pour ce vol.");
                            }
                            checkAvailableSeats(conn, volId, typeSiegeId, nombre);
                            reservation.addTypeSiegeAndNombre(volSiege.getTypeSiege(), nombre);
                            volSiege.setNombre(volSiege.getNombre() - nombre);
                            volSiege.update(conn);
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Format de paramètre invalide: " + pair);
                    }
                }
            }
        }

        if (reservation.getTypeSieges().isEmpty()) {
            throw new IllegalArgumentException("Aucune place sélectionnée pour la réservation.");
        }

        reservation.insert(conn);
        conn.commit();

        mv.setRedirect(true);
        mv.setUrl("../user-vol");

    } catch (Exception e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        mv.addObject("errorMessage", "Erreur lors de la réservation: " + e.getMessage());
        mv.setUrl("/frontoffice/volDetails.jsp?volId=" + volId);
        e.printStackTrace();
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
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


    public void checkAvailableSeats(Connection conn, int volId, int typeSiegeId, int requestedSeats) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        PreparedStatement stReserved = null;
        ResultSet resReserved = null;
        boolean creatingConn = false;

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            // Récupérer le nombre total de sièges disponibles pour ce type dans VolSiege
            String sql = "SELECT nombre FROM VolSiege WHERE id_vol = ? AND id_typeSiege = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, volId);
            st.setInt(2, typeSiegeId);
            res = st.executeQuery();

            int totalSeats = 0;
            if (res.next()) {
                totalSeats = res.getInt("nombre");
            } else {
                throw new IllegalArgumentException("Aucune information de siège trouvée pour le vol ID " + volId
                        + " et le type de siège ID " + typeSiegeId);
            }

            // Récupérer le nombre de sièges déjà réservés pour ce vol et ce type de siège
            String sqlReserved = "SELECT COALESCE(SUM(nombre), 0) as reserved FROM Reservation WHERE id_vol = ? AND id_typeSiege = ?";
            stReserved = conn.prepareStatement(sqlReserved);
            stReserved.setInt(1, volId);
            stReserved.setInt(2, typeSiegeId);
            resReserved = stReserved.executeQuery();

            int reservedSeats = 0;
            if (resReserved.next()) {
                reservedSeats = resReserved.getInt("reserved");
            }

            // Calculer les sièges réellement disponibles
            int availableSeats = totalSeats - reservedSeats;

            if (requestedSeats > availableSeats) {
                TypeSiege typeSiege = TypeSiege.getById(conn, typeSiegeId);
                throw new IllegalArgumentException(
                        "Nombre de places demandées (" + requestedSeats + ") dépasse les places disponibles ("
                                + availableSeats + ") pour le type de siège " + typeSiege.getNom());
            }

        } finally {
            if (resReserved != null)
                resReserved.close();
            if (stReserved != null)
                stReserved.close();
            if (res != null)
                res.close();
            if (st != null)
                st.close();
            if (creatingConn && conn != null)
                conn.close();
        }
    }

    @Get
    @Url("/user-resa")
    public ModelView showReservations() throws Exception {
        Connection conn = null;
        ModelView mv = new ModelView();

        try {
            conn = Database.getConnection();
            Utilisateur user = (Utilisateur) session.get("user");
            if (user == null) {
                mv.setUrl("/frontoffice/login.jsp");
                mv.setRedirect(true);
                return mv;
            }
            List<Reservation> reservations = Reservation.getByUtilisateurId(conn, user.getId());
            mv.addObject("reservations", reservations);
            mv.setUrl("/frontoffice/reservations.jsp");
        } catch (Exception e) {
            mv.addObject("errorMessage", "Erreur lors du chargement des réservations: " + e.getMessage());
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

@Post
@Url("/user-vol/cancel")
public ModelView cancelReservation(@RequestParameter("reservationId") Integer reservationId) throws Exception {
    Connection conn = null;
    ModelView mv = new ModelView();

    try {
        conn = Database.getConnection();
        conn.setAutoCommit(false);

        // Récupérer la réservation
        Reservation reservation = null;
        PreparedStatement st = conn.prepareStatement("SELECT * FROM Reservation WHERE id = ?");
        st.setInt(1, reservationId);
        ResultSet res = st.executeQuery();
        List<Reservation> tempReservations = new ArrayList<>();

        while (res.next()) {
            Reservation temp = new Reservation();
            temp.setId(res.getInt("id"));
            temp.setVol(Vol.getById(conn, res.getInt("id_vol")));
            temp.setUtilisateur(Utilisateur.getById(conn, res.getInt("id_utilisateur")));
            temp.addTypeSiegeAndNombre(TypeSiege.getById(conn, res.getInt("id_typeSiege")), res.getInt("nombre"));
            temp.setDate(res.getTimestamp("date"));
            tempReservations.add(temp);
        }
        res.close();
        st.close();

        if (!tempReservations.isEmpty()) {
            reservation = tempReservations.get(0);
            for (int i = 1; i < tempReservations.size(); i++) {
                Reservation temp = tempReservations.get(i);
                reservation.getTypeSieges().addAll(temp.getTypeSieges());
                reservation.getNombres().addAll(temp.getNombres());
            }
        }

        if (reservation == null) {
            mv.addObject("errorMessage", "Réservation avec l'ID " + reservationId + " non trouvée.");
            mv.setUrl("/frontoffice/reservations.jsp");
            return mv;
        }

        // Vérification de l'heure d'annulation
        ReservationConfig config = ReservationConfig.getLatest(conn);
        if (config != null) {
            long currentTime = System.currentTimeMillis();
            long volDepartureTime = reservation.getVol().getDepart().getTime();
            long hoursBeforeFlight = (volDepartureTime - currentTime) / (1000 * 60 * 60); // Conversion en heures

            if (hoursBeforeFlight < config.getHeureAnnulation()) {
                mv.addObject("errorMessage", 
                    "L'annulation ne peut pas être effectuée. Il faut annuler au moins " + 
                    config.getHeureAnnulation() + " heures avant le départ du vol.");
                mv.setUrl("/frontoffice/reservations.jsp");
                return mv;
            }
        }

        // Supprimer la réservation et mettre à jour les sièges
        Reservation.deleteById(conn, reservationId);

        conn.commit();

        mv.setRedirect(true);
        mv.setUrl("../user-resa");

    } catch (Exception e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        mv.addObject("errorMessage", "Erreur lors de l'annulation de la réservation : " + e.getMessage());
        mv.setUrl("/frontoffice/reservations.jsp");
        e.printStackTrace();
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                mv.addObject("errorMessage", "Erreur lors de la fermeture de la connexion : " + e.getMessage());
                mv.setUrl("/frontoffice/error.jsp");
                e.printStackTrace();
            }
        }
    }

    return mv;
}

    @Post
    @Url("/user-resa/pay")
    public ModelView payReservation(
            @RequestParameter("reservationId") Integer reservationId) throws Exception {
        Connection conn = null;
        ModelView mv = new ModelView();

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // Récupérer la réservation
            Reservation reservation = Reservation.getById(conn, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Réservation avec l'ID " + reservationId + " non trouvée.");
            }

            // Vérifier l'utilisateur connecté
            Utilisateur user = (Utilisateur) session.get("user");
            if (user == null || user.getId() != reservation.getUtilisateur().getId()) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à effectuer un paiement pour cette réservation.");
            }

            // Créer l'objet Paiement avec la date actuelle
            Paiement paiement = new Paiement();
            paiement.setReservation(reservation);
            paiement.setDatePaiement(new java.sql.Date(System.currentTimeMillis()));

            // Insérer le paiement dans la base de données
            paiement.insert(conn);

            conn.commit();

            mv.setRedirect(true);
            mv.setUrl("../user-resa");
            mv.addObject("successMessage", "Paiement effectué avec succès pour la réservation ID " + reservationId);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            mv.addObject("errorMessage", "Erreur lors du paiement : " + e.getMessage());
            mv.setUrl("/frontoffice/reservations.jsp");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    mv.addObject("errorMessage", "Erreur lors de la fermeture de la connexion : " + e.getMessage());
                    mv.setUrl("/frontoffice/error.jsp");
                    e.printStackTrace();
                }
            }
        }

        return mv;
    }

}
