package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Role {

    private int id;
    private String nom;
    
    public int getid() {
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

    public static Role getById(Connection connex, int id) throws SQLException {
        PreparedStatement st = null;
        ResultSet res = null;
        Boolean creatingConn= false;
        Role role = null;

        try {
            if (connex==null) {
                connex = Database.getConnection();
                creatingConn= true;
            }
            String sql = "SELECT * FROM Role WHERE id=?";
            st = connex.prepareStatement(sql);
            st.setInt(1, id);
            res = st.executeQuery();

            if (res.next()) {
                role= new Role();
                role.setNom(res.getString("nom"));
                role.setId(res.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (creatingConn) connex.close();
        }

        return role;
    }

    public static void main(String[] args) throws SQLException {
        Role role= Role.getById(null, 1);
        System.out.println(role.getNom());
    }
    
}
