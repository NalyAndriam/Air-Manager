package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private int id;
    private Vol vol;
    private Utilisateur utilisateur;
    private List<TypeSiege> typeSieges;
    private List<Integer> nombres;
    private Timestamp date;

    // Constructeurs
    public Reservation() {
        this.typeSieges = new ArrayList<>();
        this.nombres = new ArrayList<>();
    }

    public Reservation(int id, Vol vol, Utilisateur utilisateur, List<TypeSiege> typeSieges, List<Integer> nombres, Timestamp date) {
        this.id = id;
        this.vol = vol;
        this.utilisateur = utilisateur;
        this.typeSieges = (typeSieges != null) ? typeSieges : new ArrayList<>();
        this.nombres = (nombres != null) ? nombres : new ArrayList<>();
        this.date = date;
    }

    // Constructeur pour compatibilité avec l'ancienne structure
    public Reservation(int id, Vol vol, Utilisateur utilisateur, TypeSiege typeSiege, int nombre, Timestamp date) {
        this.id = id;
        this.vol = vol;
        this.utilisateur = utilisateur;
        this.typeSieges = new ArrayList<>();
        this.nombres = new ArrayList<>();
        if (typeSiege != null && nombre > 0) {
            this.typeSieges.add(typeSiege);
            this.nombres.add(nombre);
        }
        this.date = date;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<TypeSiege> getTypeSieges() {
        return typeSieges;
    }

    public void setTypeSieges(List<TypeSiege> typeSieges) {
        this.typeSieges = (typeSieges != null) ? typeSieges : new ArrayList<>();
    }

    public List<Integer> getNombres() {
        return nombres;
    }

    public void setNombres(List<Integer> nombres) {
        this.nombres = (nombres != null) ? nombres : new ArrayList<>();
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    // Méthodes pour ajouter un TypeSiege et un nombre
    public void addTypeSiegeAndNombre(TypeSiege typeSiege, int nombre) {
        if (typeSiege != null && nombre > 0) {
            this.typeSieges.add(typeSiege);
            this.nombres.add(nombre);
        }
    }

    // Getters et Setters dépréciés pour l'ancienne structure
    @Deprecated
    public TypeSiege getTypeSiege() {
        return typeSieges.isEmpty() ? null : typeSieges.get(0);
    }

    @Deprecated
    public void setTypeSiege(TypeSiege typeSiege) {
        if (typeSieges.isEmpty()) {
            typeSieges.add(typeSiege);
        } else {
            typeSieges.set(0, typeSiege);
        }
    }

    @Deprecated
    public int getNombre() {
        return nombres.isEmpty() ? 0 : nombres.get(0);
    }

    @Deprecated
    public void setNombre(int nombre) {
        if (nombres.isEmpty()) {
            nombres.add(nombre);
        } else {
            nombres.set(0, nombre);
        }
    }

    // Insertion dans la base de données
    public List<Integer> insert(Connection conn) throws Exception {
        PreparedStatement st = null;
        ResultSet generatedKeys = null;
        boolean creatingConn = false;
        List<Integer> generatedIds = new ArrayList<>();

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            // Vérifier s'il existe déjà une réservation pour cet utilisateur et ce vol
            Reservation existingReservation = getByUtilisateurAndVolId(conn, this.getUtilisateur().getId(), this.getVol().getId());
            if (existingReservation != null) {
                throw new SQLException("Une réservation existe déjà pour cet utilisateur et ce vol.");
            }

            if (typeSieges.size() != nombres.size()) {
                throw new IllegalStateException("Le nombre de TypeSiege et de nombres doit être identique.");
            }

            String sql = "INSERT INTO Reservation (id_vol, id_utilisateur, id_typeSiege, nombre, date) VALUES (?, ?, ?, ?, ?)";
            st = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < typeSieges.size(); i++) {
                st.setInt(1, this.getVol().getId());
                st.setInt(2, this.getUtilisateur().getId());
                st.setInt(3, this.getTypeSieges().get(i).getId());
                st.setInt(4, this.getNombres().get(i));
                st.setTimestamp(5, this.getDate());
                st.executeUpdate();

                generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedIds.add(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de récupération de l'ID généré pour TypeSiege " + typeSieges.get(i).getId());
                }
                if (generatedKeys != null) generatedKeys.close();
                generatedKeys = null;
            }

            return generatedIds;

        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    // Récupérer toutes les réservations d'un utilisateur
    public static List<Reservation> getByUtilisateurId(Connection conn, int utilisateurId) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        List<Reservation> reservations = new ArrayList<>();
        List<Reservation> tempReservations = new ArrayList<>();

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Reservation WHERE id_utilisateur = ? ORDER BY id_vol, date, id";
            st = conn.prepareStatement(sql);
            st.setInt(1, utilisateurId);
            res = st.executeQuery();

            while (res.next()) {
                Reservation temp = new Reservation();
                temp.setId(res.getInt("id"));
                temp.setVol(Vol.getById(conn, res.getInt("id_vol")));
                temp.setUtilisateur(Utilisateur.getById(conn, res.getInt("id_utilisateur")));
                temp.addTypeSiegeAndNombre(TypeSiege.getById(conn, res.getInt("id_typeSiege")), res.getInt("nombre"));
                temp.setDate(res.getTimestamp("date"));
                tempReservations.add(temp);
            }

            // Regrouper les réservations par id_vol et date
            for (Reservation temp : tempReservations) {
                boolean merged = false;
                for (Reservation reservation : reservations) {
                    if (reservation.getVol().getId() == temp.getVol().getId() &&
                        reservation.getUtilisateur().getId() == temp.getUtilisateur().getId() &&
                        reservation.getDate().equals(temp.getDate())) {
                        reservation.getTypeSieges().addAll(temp.getTypeSieges());
                        reservation.getNombres().addAll(temp.getNombres());
                        merged = true;
                        break;
                    }
                }
                if (!merged) {
                    reservations.add(temp);
                }
            }

            return reservations;

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    // Récupérer la réservation d'un utilisateur pour un vol spécifique
    public static Reservation getByUtilisateurAndVolId(Connection conn, int utilisateurId, int volId) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        Reservation reservation = null;
        List<Reservation> tempReservations = new ArrayList<>();

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Reservation WHERE id_utilisateur = ? AND id_vol = ? ORDER BY date, id";
            st = conn.prepareStatement(sql);
            st.setInt(1, utilisateurId);
            st.setInt(2, volId);
            res = st.executeQuery();

            while (res.next()) {
                Reservation temp = new Reservation();
                temp.setId(res.getInt("id"));
                temp.setVol(Vol.getById(conn, res.getInt("id_vol")));
                temp.setUtilisateur(Utilisateur.getById(conn, res.getInt("id_utilisateur")));
                temp.addTypeSiegeAndNombre(TypeSiege.getById(conn, res.getInt("id_typeSiege")), res.getInt("nombre"));
                temp.setDate(res.getTimestamp("date"));
                tempReservations.add(temp);
            }

            // Regrouper les réservations par id_vol, id_utilisateur et date
            if (!tempReservations.isEmpty()) {
                reservation = tempReservations.get(0);
                for (int i = 1; i < tempReservations.size(); i++) {
                    Reservation temp = tempReservations.get(i);
                    if (reservation.getVol().getId() == temp.getVol().getId() &&
                        reservation.getUtilisateur().getId() == temp.getUtilisateur().getId() &&
                        reservation.getDate().equals(temp.getDate())) {
                        reservation.getTypeSieges().addAll(temp.getTypeSieges());
                        reservation.getNombres().addAll(temp.getNombres());
                    } else {
                        throw new SQLException("Plusieurs réservations trouvées pour le même utilisateur et vol avec des dates différentes.");
                    }
                }
            }

            return reservation;

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    // Supprimer une réservation par son ID et mettre à jour les sièges disponibles
    public static void deleteById(Connection conn, int reservationId) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            // Récupérer les informations de la réservation pour mettre à jour VolSiege
            String sql = "SELECT id_vol, id_typeSiege, nombre FROM Reservation WHERE id = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, reservationId);
            res = st.executeQuery();

            List<Integer> volIds = new ArrayList<>();
            List<Integer> typeSiegeIds = new ArrayList<>();
            List<Integer> nombres = new ArrayList<>();

            while (res.next()) {
                volIds.add(res.getInt("id_vol"));
                typeSiegeIds.add(res.getInt("id_typeSiege"));
                nombres.add(res.getInt("nombre"));
            }

            if (volIds.isEmpty()) {
                throw new SQLException("Réservation avec l'ID " + reservationId + " non trouvée.");
            }

            // Mettre à jour les sièges disponibles dans VolSiege
            for (int i = 0; i < volIds.size(); i++) {
                VolSiege volSiege = VolSiege.getByVolIdAndTypeSiegeId(conn, volIds.get(i), typeSiegeIds.get(i));
                if (volSiege != null) {
                    volSiege.setNombre(volSiege.getNombre() + nombres.get(i));
                    volSiege.update(conn);
                }
            }

            // Supprimer la réservation
            sql = "DELETE FROM Reservation WHERE id = ?";
            st.close();
            st = conn.prepareStatement(sql);
            st.setInt(1, reservationId);
            int rowsAffected = st.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Échec de la suppression de la réservation avec l'ID " + reservationId);
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }
}