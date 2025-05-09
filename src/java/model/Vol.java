package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    public static List<Vol> getAll(Connection connex) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        List<Vol> vols = new ArrayList<>();

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Vol";
            st = connex.prepareStatement(sql);
            res = st.executeQuery();

            while (res.next()) {
                Vol vol = new Vol();
                vol.setId(res.getInt("id"));

                int idAvion = res.getInt("id_avion");
                int idVilleDepart = res.getInt("id_ville_depart");
                int idVilleArrivee = res.getInt("id_ville_arrivee");

                vol.setAvion(Avion.getById(connex, idAvion));
                vol.setVilleDepart(Ville.getById(connex, idVilleDepart));
                vol.setVilleArrivee(Ville.getById(connex, idVilleArrivee));

                vol.setDepart(res.getTimestamp("depart"));
                vol.setArrivee(res.getTimestamp("arrivee"));

                vols.add(vol);
            }

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn && connex != null) connex.close();
        }

        return vols;
    }

    public static List<Vol> search(Connection connex, int idVilleDepart, int idVilleArrivee, 
                               String dateDepart, String dateArrivee, 
                               double prixMin, double prixMax) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        List<Vol> all = new ArrayList<>();

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            StringBuilder sql = new StringBuilder("SELECT v.* FROM v_volPrix v WHERE 1=1");

            // Ajout des conditions dynamiques
            if (idVilleDepart > 0) {
                sql.append(" AND id_ville_depart = ?");
            }
            if (idVilleArrivee > 0) {
                sql.append(" AND id_ville_arrivee = ?");
            }
            if (dateDepart != null && !dateDepart.isEmpty()) {
                sql.append(" AND DATE(depart) = ?");
            }
            if (dateArrivee != null && !dateArrivee.isEmpty()) {
                sql.append(" AND DATE(arrivee) = ?");
            }
            if (prixMin > 0) {
                sql.append(" AND prix_min >= ?");
            }
            if (prixMax > 0) {
                sql.append(" AND prix_max <= ?");
            }

            System.out.println("Requête SQL : " + sql);

            st = connex.prepareStatement(sql.toString());

            // Paramétrage des valeurs
            int paramIndex = 1;
            if (idVilleDepart > 0) {
                st.setInt(paramIndex++, idVilleDepart);
            }
            if (idVilleArrivee > 0) {
                st.setInt(paramIndex++, idVilleArrivee);
            }
            if (dateDepart != null && !dateDepart.isEmpty()) {
                st.setDate(paramIndex++, java.sql.Date.valueOf(dateDepart));
            }
            if (dateArrivee != null && !dateArrivee.isEmpty()) {
                st.setDate(paramIndex++, java.sql.Date.valueOf(dateArrivee));
            }
            if (prixMin > 0) {
                st.setDouble(paramIndex++, prixMin);
            }
            if (prixMax > 0) {
                st.setDouble(paramIndex++, prixMax);
            }

            System.out.println("Exécution de la requête...");
            res = st.executeQuery();
            while (res.next()) {
                Vol vol = new Vol();
                vol.setId(res.getInt("id"));

                // Récupération des objets liés
                vol.setAvion(Avion.getById(connex, res.getInt("id_avion")));
                vol.setVilleDepart(Ville.getById(connex, res.getInt("id_ville_depart")));
                vol.setVilleArrivee(Ville.getById(connex, res.getInt("id_ville_arrivee")));

                vol.setDepart(res.getTimestamp("depart"));
                vol.setArrivee(res.getTimestamp("arrivee"));

                all.add(vol);
            }
            System.out.println("Nombre de vols trouvés : " + all.size());

        } catch (SQLException e) {
            System.err.println("Erreur SQL dans Vol.search : " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de format de date dans Vol.search : " + e.getMessage());
            throw new SQLException("Format de date invalide", e);
        } finally {
            if (res != null) try { res.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (st != null) try { st.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (creatingConn && connex != null) try { connex.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return all;
    }

    public void delete(Connection conn) throws SQLException {
        PreparedStatement st = null;
        try {
            String sql = "DELETE FROM Vol WHERE id = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, this.getId());
            st.executeUpdate();
        } finally {
            if (st != null) st.close();
        }
    }


    public static void main(String[] args) throws Exception {
        Connection conn= Database.getConnection();
        List<Vol> vols= Vol.search(conn, 0, 0, "11-04-2025", null, 0, 0);
        System.out.println(vols.size());
    }
}
