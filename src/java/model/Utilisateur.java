package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mg.emberframework.annotation.validation.Email;
import mg.emberframework.annotation.validation.Length;
import mg.emberframework.annotation.validation.Required;

public class Utilisateur {
    private Integer id;
    private String nom;

    @Required
    @Email
    private String email;

    @Required
    @Length(length = 3)
    private String mdp;
    private Role role;

    public Utilisateur() {

    }

    public Utilisateur(String nom, String email, String mdp, Role role) {
        this.nom = nom;
        this.email = email;
        this.mdp = mdp;
        this.role = role;
    }

    public Utilisateur(String email, String mdp) {
        this.email = email;
        this.mdp = mdp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer Id) {
        this.id = Id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole(Connection connex, int idRole) throws SQLException {
        this.role = Role.getById(connex, idRole);
    }

    public static Utilisateur login(Connection connex, String email, String mdp) throws SQLException {
        PreparedStatement st = null;
        ResultSet res = null;
        Boolean creatingConn = false;
        Utilisateur utilisateur = null;

        try {
            if (connex == null) {
                connex = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Utilisateur WHERE email = ? AND mdp = ?";
            st = connex.prepareStatement(sql);
            st.setString(1, email);
            st.setString(2, mdp);
            res = st.executeQuery();

            if (res.next()) {
                utilisateur = new Utilisateur();
                utilisateur.setId(res.getInt("id"));
                utilisateur.setNom(res.getString("nom"));
                utilisateur.setEmail(res.getString("email"));
                utilisateur.setMdp(res.getString("mdp"));
                utilisateur.setRole(connex, res.getInt("id_role"));
                System.out.println(res.getInt("id_role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res != null)
                res.close();
            if (st != null)
                st.close();
            if (creatingConn)
                connex.close();
        }

        return utilisateur;
    }

    public static Utilisateur getById(Connection conn, int id) throws Exception {
        PreparedStatement st = null;
        ResultSet res = null;
        boolean creatingConn = false;
        Utilisateur utilisateur = null;

        try {
            if (conn == null) {
                conn = Database.getConnection();
                creatingConn = true;
            }

            String sql = "SELECT * FROM Utilisateur WHERE id = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, id);
            res = st.executeQuery();

            if (res.next()) {
                utilisateur = new Utilisateur();
                utilisateur.setId(res.getInt("id"));
                utilisateur.setNom(res.getString("nom"));
                utilisateur.setEmail(res.getString("email"));
                utilisateur.setMdp(res.getString("mdp"));
                utilisateur.setRole(conn, res.getInt("id_role"));
            }

        } finally {
            if (res != null)
                res.close();
            if (st != null)
                st.close();
            if (creatingConn && conn != null)
                conn.close();
        }

        return utilisateur;
    }

    public static void main(String[] args) throws Exception {

        Connection conn = Database.getConnection();

        Utilisateur utilisateur = Utilisateur.login(conn, "rakoto@gmail.com", "123");
        System.out.println(utilisateur.getRole().getNom());
    }
}
