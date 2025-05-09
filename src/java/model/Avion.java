package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Avion {
    private int id;
    private String modele;
    private double capacite;
    private Date dateFabrication;

    public Avion() {}

    public Avion(int id, String modele, double capacite, Date dateFabrication) {
        this.id = id;
        this.modele = modele;
        this.capacite = capacite;
        this.dateFabrication = dateFabrication;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public double getCapacite() {
        return capacite;
    }

    public void setCapacite(double capacite) {
        this.capacite = capacite;
    }

    public Date getDateFabrication() {
        return dateFabrication;
    }

    public void setDateFabrication(Date dateFabrication) {
        this.dateFabrication = dateFabrication;
    }

    public static Avion getById(Connection connex, int id) throws SQLException {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        Avion avion = null;

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Avion WHERE id = ?";
            st = connex.prepareStatement(sql);
            st.setInt(1, id);
            res = st.executeQuery();

            if (res.next()) {
                avion = new Avion();
                avion.setId(res.getInt("id"));
                avion.setModele(res.getString("modele"));
                avion.setCapacite(res.getDouble("capacite"));
                avion.setDateFabrication(res.getDate("date_fabrication"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn) connex.close();
        }

        return avion;
    }

    public static List<Avion> getAll(Connection connex) throws SQLException {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        List<Avion> avions = new ArrayList<>();

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Avion";
            st = connex.prepareStatement(sql);
            res = st.executeQuery();

            while (res.next()) {
                Avion avion = new Avion();
                avion.setId(res.getInt("id"));
                avion.setModele(res.getString("modele"));
                avion.setCapacite(res.getDouble("capacite"));
                avion.setDateFabrication(res.getDate("date_fabrication"));
                avions.add(avion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn) connex.close();
        }

        return avions;
    }


}

