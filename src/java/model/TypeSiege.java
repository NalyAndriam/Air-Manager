package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeSiege {
    private int id;
    private String nom;

    public TypeSiege() {}

    public TypeSiege(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public static TypeSiege getById(Connection connex, int id) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        TypeSiege type = null;

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM TypeSiege WHERE id = ?";
            st = connex.prepareStatement(sql);
            st.setInt(1, id);
            res = st.executeQuery();

            if (res.next()) {
                type = new TypeSiege();
                type.setId(res.getInt("id"));
                type.setNom(res.getString("nom"));
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn && connex != null) connex.close();
        }

        return type;
    }

    public static List<TypeSiege> getAll(Connection connex) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        List<TypeSiege> all = new ArrayList<>();

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM TypeSiege";
            st = connex.prepareStatement(sql);
            res = st.executeQuery();

            while (res.next()) {
                TypeSiege type = new TypeSiege();
                type.setId(res.getInt("id"));
                type.setNom(res.getString("nom"));
                all.add(type);
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn && connex != null) connex.close();
        }

        return all;
    }
}
