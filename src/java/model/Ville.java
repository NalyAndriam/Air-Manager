package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Ville {
    private int id;
    private String nom;

    public Ville() {}

    public Ville(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public static Ville getById(Connection connex, int id) throws SQLException {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        Ville ville = null;

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Ville WHERE id = ?";
            st = connex.prepareStatement(sql);
            st.setInt(1, id);
            res = st.executeQuery();

            if (res.next()) {
                ville = new Ville();
                ville.setId(res.getInt("id"));
                ville.setNom(res.getString("nom"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn) connex.close();
        }

        return ville;
    }

    public static List<Ville> getAll(Connection connex) throws SQLException {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        List<Ville> villes = new ArrayList<>();

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Ville";
            st = connex.prepareStatement(sql);
            res = st.executeQuery();

            while (res.next()) {
                Ville ville = new Ville();
                ville.setId(res.getInt("id"));
                ville.setNom(res.getString("nom"));
                villes.add(ville);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn) connex.close();
        }

        return villes;
    }


}

