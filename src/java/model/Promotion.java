package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Promotion {
    private int id;
    private Vol vol;
    private TypeSiege typeSiege;
    private double nombre;
    private double pourcentage;

    // Constructeurs
    public Promotion() {}

    public Promotion(int id, Vol vol, TypeSiege typeSiege, double nombre, double pourcentage) {
        this.id = id;
        this.vol = vol;
        this.typeSiege = typeSiege;
        this.nombre = nombre;
        this.pourcentage = pourcentage;
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

    public TypeSiege getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(TypeSiege typeSiege) {
        this.typeSiege = typeSiege;
    }

    public double getNombre() {
        return nombre;
    }

    public void setNombre(double nombre) {
        this.nombre = nombre;
    }

    public double getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(double pourcentage) {
        this.pourcentage = pourcentage;
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

            String sql = "INSERT INTO Promotion (id_vol, id_typeSiege, nombre, pourcentage) VALUES (?, ?, ?, ?)";
            st = conn.prepareStatement(sql);
            st.setInt(1, this.getVol().getId());
            st.setInt(2, this.getTypeSiege().getId());
            st.setDouble(3, this.getNombre());
            st.setDouble(4, this.getPourcentage());

            st.executeUpdate();

        } finally {
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    // Récupérer les dernières promotions pour un vol donné
    public static List<Promotion> getLatestByVolId(Connection conn, int volId) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        List<Promotion> promotions = new ArrayList<>();

        try {
            String sql = "SELECT p.* FROM Promotion p " +
                         "WHERE p.id_vol = ? AND p.id = (" +
                         "SELECT MAX(id) FROM Promotion WHERE id_vol = p.id_vol AND id_typeSiege = p.id_typeSiege" +
                         ")";
            st = conn.prepareStatement(sql);
            st.setInt(1, volId);
            res = st.executeQuery();

            while (res.next()) {
                Promotion promo = new Promotion();
                promo.setId(res.getInt("id"));
                promo.setVol(Vol.getById(conn, res.getInt("id_vol")));
                promo.setTypeSiege(TypeSiege.getById(conn, res.getInt("id_typeSiege")));
                promo.setNombre(res.getDouble("nombre"));
                promo.setPourcentage(res.getDouble("pourcentage"));
                promotions.add(promo);
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
        }

        return promotions;
    }
}