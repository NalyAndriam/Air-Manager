package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReservationPromo {
    private int idVol;
    private String typeSiege;
    private double prixUnitaire;
    private int totalPlaces;
    private double montantTotal;
    private Timestamp dateButoire;

    // Constructeurs
    public ReservationPromo() {
    }

    public ReservationPromo(int idVol, String typeSiege, double prixUnitaire, int totalPlaces, 
                           double montantTotal, Timestamp dateButoire) {
        this.idVol = idVol;
        this.typeSiege = typeSiege;
        this.prixUnitaire = prixUnitaire;
        this.totalPlaces = totalPlaces;
        this.montantTotal = montantTotal;
        this.dateButoire = dateButoire;
    }

    // Getters et Setters
    public int getIdVol() {
        return idVol;
    }

    public void setIdVol(int idVol) {
        this.idVol = idVol;
    }

    public String getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(String typeSiege) {
        this.typeSiege = typeSiege;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public int getTotalPlaces() {
        return totalPlaces;
    }

    public void setTotalPlaces(int totalPlaces) {
        this.totalPlaces = totalPlaces;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Timestamp getDateButoire() {
        return dateButoire;
    }

    public void setDateButoire(Timestamp dateButoire) {
        this.dateButoire = dateButoire;
    }

    // Méthode pour récupérer toutes les réservations promotionnelles
    public static List<ReservationPromo> getAll(Connection conn) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        List<ReservationPromo> reservationsPromo = new ArrayList<>();

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM v_resa_promo_grouped";
            st = conn.prepareStatement(sql);
            res = st.executeQuery();

            while (res.next()) {
                ReservationPromo rp = new ReservationPromo();
                rp.setIdVol(res.getInt("id_vol"));
                rp.setTypeSiege(res.getString("type_siege"));
                rp.setPrixUnitaire(res.getDouble("prix_unitaire"));
                rp.setTotalPlaces(res.getInt("total_places"));
                rp.setMontantTotal(res.getDouble("montant_total"));
                rp.setDateButoire(res.getTimestamp("date_butoire"));
                reservationsPromo.add(rp);
            }

            return reservationsPromo;

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    public static void main(String[] args) throws Exception {
        Connection conn= Database.getConnection();
        List<ReservationPromo> res= ReservationPromo.getAll(conn);
        System.out.println(res.get(0).typeSiege);
    }
}