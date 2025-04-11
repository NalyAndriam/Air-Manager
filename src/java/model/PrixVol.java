package model;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PrixVol {
    private int id;
    private Vol vol;
    private TypeSiege typeSiege;
    private double prix;

    public PrixVol() {}

    public PrixVol(int id, Vol vol, TypeSiege typeSiege, double prix) {
        this.id = id;
        this.vol = vol;
        this.typeSiege = typeSiege;
        this.prix = prix;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Vol getVol() { return vol; }
    public void setVol(Vol vol) { this.vol = vol; }

    public TypeSiege getTypeSiege() { return typeSiege; }
    public void setTypeSiege(TypeSiege typeSiege) { this.typeSiege = typeSiege; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public void insert(Connection connex) throws Exception {
        PreparedStatement st = null;
        boolean creatingConn = false;

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "INSERT INTO PrixVol (id_vol, id_typeSiege, prix) VALUES (?, ?, ?)";
            st = connex.prepareStatement(sql);
            st.setInt(1, this.getVol().getId());
            st.setInt(2, this.getTypeSiege().getId());
            st.setDouble(3, this.getPrix());

            st.executeUpdate();

        } finally {
            if (st != null) st.close();
            if (creatingConn && connex != null) connex.close();
        }
    }
}
