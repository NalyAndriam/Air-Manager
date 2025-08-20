package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PromotionAlea {
    private int id;
    private double nombre;
    private Date dateFin;
    private double prix;
    private TypeSiege typeSiege;
    private Vol vol;

    // Constructeurs
    public PromotionAlea() {}

    public PromotionAlea(int id, double nombre, Date dateFin, double prix, TypeSiege typeSiege, Vol vol) {
        this.id = id;
        this.nombre = nombre;
        this.dateFin = dateFin;
        this.prix = prix;
        this.typeSiege = typeSiege;
        this.vol = vol;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getNombre() {
        return nombre;
    }

    public void setNombre(double nombre) {
        this.nombre = nombre;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public TypeSiege getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(TypeSiege typeSiege) {
        this.typeSiege = typeSiege;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
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

            String sql = "INSERT INTO Promotion_Alea (nombre, date_fin, prix, id_typeSiege, id_vol) VALUES (?, ?, ?, ?, ?)";
            st = conn.prepareStatement(sql);
            st.setDouble(1, this.getNombre());
            st.setDate(2, new java.sql.Date(this.getDateFin().getTime()));
            st.setDouble(3, this.getPrix());
            st.setInt(4, this.getTypeSiege().getId());
            st.setInt(5, this.getVol().getId());

            st.executeUpdate();

        } finally {
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    // Récupérer les promotions aléatoires pour un vol donné
    public static List<PromotionAlea> getByVolId(Connection conn, int volId) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        List<PromotionAlea> promotions = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Promotion_Alea WHERE id_vol = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, volId);
            res = st.executeQuery();

            while (res.next()) {
                PromotionAlea promo = new PromotionAlea();
                promo.setId(res.getInt("id"));
                promo.setNombre(res.getDouble("nombre"));
                promo.setDateFin(res.getDate("date_fin"));
                promo.setPrix(res.getDouble("prix"));
                promo.setTypeSiege(TypeSiege.getById(conn, res.getInt("id_typeSiege")));
                promo.setVol(Vol.getById(conn, res.getInt("id_vol")));
                promotions.add(promo);
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
        }

        return promotions;
    }

    // Supprimer les promotions aléatoires pour un vol donné
    public static void deleteByVolId(Connection conn, int volId) throws Exception {
        PreparedStatement st = null;
        boolean creatingConn = false;

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "DELETE FROM Promotion_Alea WHERE id_vol = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, volId);
            st.executeUpdate();

        } finally {
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }
}