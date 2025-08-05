package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationConfig {
    private int id;
    private double heureReservation;
    private double heureAnnulation;

    // Constructeurs
    public ReservationConfig() {}

    public ReservationConfig(int id, double heureReservation, double heureAnnulation) {
        this.id = id;
        this.heureReservation = heureReservation;
        this.heureAnnulation = heureAnnulation;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getHeureReservation() {
        return heureReservation;
    }

    public void setHeureReservation(double heureReservation) {
        this.heureReservation = heureReservation;
    }

    public double getHeureAnnulation() {
        return heureAnnulation;
    }

    public void setHeureAnnulation(double heureAnnulation) {
        this.heureAnnulation = heureAnnulation;
    }

    public void insert(Connection conn) throws Exception {
        PreparedStatement st = null;
        boolean creatingConn = false;

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "INSERT INTO ReservationConfig (heure_reservation, heure_annulation) VALUES (?, ?)";
            st = conn.prepareStatement(sql);
            st.setDouble(1, this.getHeureReservation());
            st.setDouble(2, this.getHeureAnnulation());

            st.executeUpdate();

        } finally {
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    public static ReservationConfig getLatest(Connection conn) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        ReservationConfig config = null;

        try {
            String sql = "SELECT * FROM ReservationConfig ORDER BY id DESC LIMIT 1";
            st = conn.prepareStatement(sql);
            res = st.executeQuery();

            if (res.next()) {
                config = new ReservationConfig();
                config.setId(res.getInt("id"));
                config.setHeureReservation(res.getDouble("heure_reservation"));
                config.setHeureAnnulation(res.getDouble("heure_annulation"));
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
        }

        return config;
    }
}