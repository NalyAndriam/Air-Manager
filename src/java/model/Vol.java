package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import mg.emberframework.annotation.validation.Required;

public class Vol {

    private int id;
    private Avion avion;
    private Ville villeDepart;
    private Ville villeArrivee;

    @Required
    private Timestamp depart;

    @Required
    private Timestamp arrivee;

    public Vol() {}

    public Vol(int id, Avion avion, Ville villeDepart, Ville villeArrivee, Timestamp depart, Timestamp arrivee) {
        this.id = id;
        this.avion = avion;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.depart = depart;
        this.arrivee = arrivee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public Ville getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(Ville villeDepart) {
        this.villeDepart = villeDepart;
    }

    public Ville getVilleArrivee() {
        return villeArrivee;
    }

    public void setVilleArrivee(Ville villeArrivee) {
        this.villeArrivee = villeArrivee;
    }

    public Timestamp getDepart() {
        return depart;
    }

    public void setDepart(Timestamp depart) {
        this.depart = depart;
    }

    public Timestamp getArrivee() {
        return arrivee;
    }

    public void setArrivee(Timestamp arrivee) {
        this.arrivee = arrivee;
    }

    public int insert(Connection conn) throws Exception {
        PreparedStatement st = null;
        ResultSet generatedKeys = null;
        boolean creatingConn = false;
        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }
            String sql = "INSERT INTO Vol (id_avion, id_ville_depart, id_ville_arrivee, depart, arrivee) VALUES (?, ?, ?, ?, ?)";
            st = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, this.getAvion().getId());
            st.setInt(2, this.getVilleDepart().getId());
            st.setInt(3, this.getVilleArrivee().getId());
            st.setTimestamp(4, this.getDepart());
            st.setTimestamp(5, this.getArrivee());
            st.executeUpdate();
            generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            throw new SQLException("Échec de récupération de l'ID généré.");
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }

    public static Vol getById(Connection connex, int id) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Vol WHERE id = ?";
            st = connex.prepareStatement(sql);
            st.setInt(1, id);

            res = st.executeQuery();

            if (res.next()) {
                Vol vol = new Vol();
                vol.setId(res.getInt("id"));

                int idAvion = res.getInt("id_avion");
                int idVilleDepart = res.getInt("id_ville_depart");
                int idVilleArrivee = res.getInt("id_ville_arrivee");

                // Appelle les méthodes statiques pour récupérer les objets liés
                vol.setAvion(Avion.getById(connex, idAvion));
                vol.setVilleDepart(Ville.getById(connex, idVilleDepart));
                vol.setVilleArrivee(Ville.getById(connex, idVilleArrivee));

                vol.setDepart(res.getTimestamp("depart"));
                vol.setArrivee(res.getTimestamp("arrivee"));

                return vol;
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn && connex != null) connex.close();
        }

        return null; // Si aucun résultat trouvé
    }

}
