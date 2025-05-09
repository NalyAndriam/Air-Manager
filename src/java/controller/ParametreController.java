package controller;

import java.sql.Connection;

import mg.emberframework.annotation.http.Controller;
import mg.emberframework.annotation.http.Get;
import mg.emberframework.annotation.http.Post;
import mg.emberframework.annotation.http.RequestParameter;
import mg.emberframework.annotation.http.Url;
import mg.emberframework.core.data.ModelView;
import model.Database;
import model.ReservationConfig;

@Controller
public class ParametreController {
    
    @Get
    @Url("/settings/reservation")
    public ModelView showConfig() throws Exception {
        Connection conn = null;
        ModelView mv = new ModelView();

        try {
            conn = Database.getConnection();
            ReservationConfig config = ReservationConfig.getLatest(conn);
            mv.addObject("config", config);
            mv.setUrl("/backoffice/reservationConfig.jsp");
        } catch (Exception e) {
            mv.addObject("errorMessage", "Erreur lors du chargement de la configuration: " + e.getMessage());
            mv.setUrl("/backoffice/error.jsp");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    mv.addObject("errorMessage", "Erreur lors de la fermeture de la connexion: " + e.getMessage());
                    mv.setUrl("/backoffice/error.jsp");
                    e.printStackTrace();
                }
            }
        }

        return mv;
    }

    @Post
    @Url("/settings/reservation")
    public ModelView insertConfig(
            @RequestParameter("heureReservation") Double heureReservation,
            @RequestParameter("heureAnnulation") Double heureAnnulation
    ) throws Exception {
        ModelView mv = new ModelView();
        Connection conn = null;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            ReservationConfig config = new ReservationConfig();
            config.setHeureReservation(heureReservation);
            config.setHeureAnnulation(heureAnnulation);
            config.insert(conn);

            conn.commit();

            mv.setRedirect(true);
            mv.setUrl("../settings/reservation");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            mv.addObject("errorMessage", "Erreur lors de l'insertion de la configuration: " + e.getMessage());
            mv.setUrl("/backoffice/reservationConfig.jsp");
            e.printStackTrace();

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    mv.addObject("errorMessage", "Erreur lors de la fermeture de la connexion: " + e.getMessage());
                    mv.setUrl("/backoffice/error.jsp");
                    e.printStackTrace();
                }
            }
        }

        return mv;
    }

}
