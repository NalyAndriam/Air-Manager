package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VolSiege {
    private int id;
    private Vol vol;
    private TypeSiege typeSiege;
    private int nombre;

    public VolSiege() {}

    public VolSiege(int id, Vol vol, TypeSiege typeSiege, int nombre) {
        this.id = id;
        this.vol = vol;
        this.typeSiege = typeSiege;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Vol getVol() { return vol; }
    public void setVol(Vol vol) { this.vol = vol; }

    public TypeSiege getTypeSiege() { return typeSiege; }
    public void setTypeSiege(TypeSiege typeSiege) { this.typeSiege = typeSiege; }

    public int getNombre() { return nombre; }
    public void setNombre(int nombre) { this.nombre = nombre; }

    public void insert(Connection connex) throws Exception {
        PreparedStatement st = null;
        boolean creatingConn = false;

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "INSERT INTO VolSiege (id_vol, id_typeSiege, nombre) VALUES (?, ?, ?)";
            st = connex.prepareStatement(sql);
            st.setInt(1, this.getVol().getId());
            st.setInt(2, this.getTypeSiege().getId());
            st.setDouble(3, this.getNombre());

            st.executeUpdate();

        } finally {
            if (st != null) st.close();
            if (creatingConn && connex != null) connex.close();
        }
    }

    public static void deleteByVolId(Connection conn, int volId) throws SQLException {
        PreparedStatement st = null;
        try {
            String sql = "DELETE FROM VolSiege WHERE id_vol = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, volId);
            st.executeUpdate();
        } finally {
            if (st != null) st.close();
        }
    }

    public static List<VolSiege> getByVolId(Connection conn, int volId) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        List<VolSiege> volSieges = new ArrayList<>();

        try {
            String sql = "SELECT * FROM VolSiege WHERE id_vol = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, volId);
            res = st.executeQuery();

            while (res.next()) {
                VolSiege volSiege = new VolSiege();
                volSiege.setVol(Vol.getById(conn, res.getInt("id_vol")));
                volSiege.setTypeSiege(TypeSiege.getById(conn, res.getInt("id_typesiege")));
                volSiege.setNombre(res.getInt("nombre"));
                volSieges.add(volSiege);
            }
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
        }

        return volSieges;
    }

    public void update(Connection conn) throws Exception {
        PreparedStatement st = null;
        boolean creatingConn = false;

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "UPDATE VolSiege SET nombre = ? WHERE id_vol = ? AND id_typeSiege = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, this.getNombre());
            st.setInt(2, this.getVol().getId());
            st.setInt(3, this.getTypeSiege().getId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de la mise à jour : aucun enregistrement trouvé pour id_vol = " + this.getVol().getId() + " et id_typeSiege = " + this.getTypeSiege().getId());
            }

        } finally {
            if (st != null) st.close();
            if (creatingConn && conn != null) conn.close();
        }
    }
}
