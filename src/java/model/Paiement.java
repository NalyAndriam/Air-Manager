package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Paiement {
    private int id;
    private Reservation reservation;
    private Date datePaiement;

    // Constructeurs
    public Paiement() {}

    public Paiement(int id, Reservation reservation, Date datePaiement) {
        this.id = id;
        this.reservation = reservation;
        this.datePaiement = datePaiement;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Date getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    // Insertion dans la base de données
    public void insert(Connection conn) throws Exception {
        PreparedStatement st = null;
        boolean creatingConn = false;

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "INSERT INTO Paiement (id_reservation, date_paiement) VALUES (?, ?)";
            st = conn.prepareStatement(sql);
            st.setInt(1, this.getReservation().getId());
            st.setDate(2, this.getDatePaiement());

            st.executeUpdate();

        } finally {
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    // Récupérer les paiements pour une réservation donnée
    public static List<Paiement> getByReservationId(Connection conn, int reservationId) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        List<Paiement> paiements = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Paiement WHERE id_reservation = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, reservationId);
            res = st.executeQuery();

            while (res.next()) {
                Paiement paiement = new Paiement();
                paiement.setId(res.getInt("id"));
                paiement.setReservation(Reservation.getById(conn, res.getInt("id_reservation")));
                paiement.setDatePaiement(res.getDate("date_paiement"));
                paiements.add(paiement);
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
        }

        return paiements;
    }

    // Supprimer les paiements pour une réservation donnée
    public static void deleteByReservationId(Connection conn, int reservationId) throws Exception {
        PreparedStatement st = null;
        boolean creatingConn = false;

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "DELETE FROM Paiement WHERE id_reservation = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, reservationId);
            st.executeUpdate();

        } finally {
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }
}