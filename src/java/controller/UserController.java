package controller;

import java.io.IOException;
import java.sql.Connection;

import mg.emberframework.annotation.http.Controller;
import mg.emberframework.annotation.http.Get;
import mg.emberframework.annotation.http.Post;
import mg.emberframework.annotation.http.RequestParameter;
import mg.emberframework.annotation.http.Url;
import mg.emberframework.core.data.File;
import mg.emberframework.core.data.ModelView;
import mg.emberframework.core.data.Session;
import model.Database;
import model.Utilisateur;

@Controller
public class UserController {
    Session session;
    
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Get
    @Url("/login")
    public ModelView login(){
        ModelView mv = new ModelView();
        mv.setUrl("./login.jsp");
        String errorMessage = (String) session.get("errorMessage");
        if (errorMessage != null) {
            mv.addObject("errorMessage", errorMessage);
            session.remove("errorMessage"); 
        }
        return mv;
    }

    @Post
    @Url("/login")
    public ModelView login(@RequestParameter("user") Utilisateur user) throws Exception {
        Connection conn = Database.getConnection();
        ModelView mv = new ModelView();

        try {
            Utilisateur utilisateur = Utilisateur.login(conn, user.getEmail(), user.getMdp());
            if (utilisateur != null) {
                String role = utilisateur.getRole().getNom();
                session.add("user", utilisateur); 
                if (role.equals("admin")) {
                    mv.setRedirect(true);
                    mv.setUrl("./vol");
                } else {
                    mv.setRedirect(true);
                    mv.setUrl("./user-vol");    
                }
            } else {
                mv.setRedirect(true);
                mv.setUrl("./login"); 
                mv.addObject("errorMessage", "Email ou mot de passe incorrect");
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        
        return mv;
    }


}
