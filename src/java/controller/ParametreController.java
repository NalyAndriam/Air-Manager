package controller;

import java.sql.Connection;
import java.util.List;

import mg.emberframework.annotation.http.Controller;
import mg.emberframework.annotation.http.Get;
import mg.emberframework.annotation.http.Post;
import mg.emberframework.annotation.http.RequestParameter;
import mg.emberframework.annotation.http.Url;
import mg.emberframework.core.data.ModelView;
import model.Database;
import model.Promotion;
import model.PromotionAlea;
import model.ReservationConfig;
import model.TypeSiege;
import model.Vol;

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
            @RequestParameter("heureAnnulation") Double heureAnnulation) throws Exception {
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

    @Get
    @Url("/settings/promotion")
    public ModelView showPromotionForm(@RequestParameter("volId") Integer volId) throws Exception {
        Connection conn = null;
        ModelView mv = new ModelView();

        try {
            conn = Database.getConnection();
            List<Vol> vols = Vol.getAll(conn);
            List<TypeSiege> types = TypeSiege.getAll(conn);
            List<Promotion> promotions = null;

            if (volId != null && volId > 0) {
                promotions = Promotion.getLatestByVolId(conn, volId);
            }

            mv.addObject("vols", vols);
            mv.addObject("types", types);
            mv.addObject("promotions", promotions);
            mv.addObject("selectedVolId", volId);
            mv.setUrl("/backoffice/promotion.jsp");

        } catch (Exception e) {
            mv.addObject("errorMessage", "Erreur lors du chargement des donnees: " + e.getMessage());
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
    @Url("/settings/promotion")
    public ModelView insertPromotion(
            @RequestParameter("volId") Integer volId,
            @RequestParameter("typeSiegeId") Integer typeSiegeId,
            @RequestParameter("nombre") Double nombre,
            @RequestParameter("pourcentage") Double pourcentage) throws Exception {
        ModelView mv = new ModelView();
        Connection conn = null;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            Vol vol = Vol.getById(conn, volId);
            if (vol == null) {
                throw new IllegalArgumentException("Vol avec l'ID " + volId + " non trouve.");
            }

            TypeSiege typeSiege = TypeSiege.getById(conn, typeSiegeId);
            if (typeSiege == null) {
                throw new IllegalArgumentException("Type de siege avec l'ID " + typeSiegeId + " non trouve.");
            }

            Promotion promotion = new Promotion();
            promotion.setVol(vol);
            promotion.setTypeSiege(typeSiege);
            promotion.setNombre(nombre);
            promotion.setPourcentage(pourcentage);
            promotion.insert(conn);

            conn.commit();

            mv.setRedirect(true);
            mv.setUrl("../settings/promotion?volId=" + volId);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            mv.addObject("errorMessage", "Erreur lors de l'insertion de la promotion: " + e.getMessage());
            mv.setUrl("/backoffice/promotion.jsp");
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

    @Get
    @Url("/settings/promotion-alea")
    public ModelView showPromotionAleaForm(@RequestParameter("volId") Integer volId) throws Exception {
        Connection conn = null;
        ModelView mv = new ModelView();

        try {
            conn = Database.getConnection();
            List<Vol> vols = Vol.getAll(conn);
            List<TypeSiege> types = TypeSiege.getAll(conn);
            List<PromotionAlea> promotions = null;

            if (volId != null && volId > 0) {
                promotions = PromotionAlea.getByVolId(conn, volId);
            }

            mv.addObject("vols", vols);
            mv.addObject("types", types);
            mv.addObject("promotions", promotions);
            mv.addObject("selectedVolId", volId);
            mv.setUrl("/backoffice/promotionAlea.jsp");

        } catch (Exception e) {
            mv.addObject("errorMessage", "Erreur lors du chargement des données: " + e.getMessage());
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
    @Url("/settings/promotion-alea")
    public ModelView insertPromotionAlea(
            @RequestParameter("volId") Integer volId,
            @RequestParameter("typeSiegeId") Integer typeSiegeId,
            @RequestParameter("nombre") Double nombre,
            @RequestParameter("prix") Double prix,
            @RequestParameter("dateFin") String dateFin) throws Exception {
        ModelView mv = new ModelView();
        Connection conn = null;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            Vol vol = Vol.getById(conn, volId);
            if (vol == null) {
                throw new IllegalArgumentException("Vol avec l'ID " + volId + " non trouvé.");
            }

            TypeSiege typeSiege = TypeSiege.getById(conn, typeSiegeId);
            if (typeSiege == null) {
                throw new IllegalArgumentException("Type de siège avec l'ID " + typeSiegeId + " non trouvé.");
            }

            // Convertir la dateFin (String) en java.util.Date
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDateFin = sdf.parse(dateFin);

            PromotionAlea promotion = new PromotionAlea();
            promotion.setVol(vol);
            promotion.setTypeSiege(typeSiege);
            promotion.setNombre(nombre);
            promotion.setPrix(prix);
            promotion.setDateFin(parsedDateFin);
            promotion.insert(conn);

            conn.commit();

            mv.setRedirect(true);
            mv.setUrl("../settings/promotion-alea?volId=" + volId);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            mv.addObject("errorMessage", "Erreur lors de l'insertion de la promotion aléatoire: " + e.getMessage());
            mv.setUrl("/backoffice/promotionAlea.jsp");
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
