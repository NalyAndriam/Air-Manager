<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.Connection" %>
<%

    Connection conn = null;
    List<ReservationPromo> reservationsPromo = null;
    try {
        conn = Database.getConnection();
        reservationsPromo = ReservationPromo.getAll(conn);
    } catch (Exception e) {
        request.setAttribute("errorMessage", "Erreur lors de la récupération des réservations promotionnelles: " + e.getMessage());
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Réservations Promotionnelles</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="../assets/css/liste.css" rel="stylesheet">
    <link href="../assets/css/sidebar.css" rel="stylesheet">
    <link href="../assets/css/navbar.css" rel="stylesheet">
    <link href="../assets/css/frontoffice.css" rel="stylesheet">
    <style>
        .main-content {
            margin-left: 250px;
            padding: 2rem;
        }
        .header {
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-bottom: 2rem;
        }
        .header-icon {
            font-size: 1.5rem;
            color: #1e90ff;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        }
        th, td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e5e7eb;
        }
        th {
            background-color: #f3f4f6;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.75rem;
            color: #374151;
        }
        .back-btn {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            margin-top: 2rem;
            padding: 0.5rem 1rem;
            background-color: #1e90ff;
            color: white;
            text-decoration: none;
            border-radius: 0.375rem;
            transition: background-color 0.3s;
        }
        .back-btn:hover {
            background-color: #1c86ee;
        }
        .error-message {
            color: red;
            font-size: smaller;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>

<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <div class="container">
        <div class="header">
            <i class="fa-solid fa-ticket header-icon"></i>
            <h1>Réservations Promotionnelles</h1>
        </div>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <i class="fa-solid fa-exclamation-circle"></i> <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <% if (reservationsPromo != null && !reservationsPromo.isEmpty()) { %>
            <div class="reservations-list">
                <table>
                    <thead>
                        <tr>
                            <th>ID Vol</th>
                            <th>Type de Siège</th>
                            <th>Prix Unitaire</th>
                            <th>Total Places</th>
                            <th>Montant Total</th>
                            <th>Date Butoire</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (ReservationPromo rp : reservationsPromo) { %>
                            <tr>
                                <td><%= rp.getIdVol() %></td>
                                <td><%= rp.getTypeSiege() %></td>
                                <td><%= String.format("%.2f", rp.getPrixUnitaire()) %> €</td>
                                <td><%= rp.getTotalPlaces() %></td>
                                <td><%= String.format("%.2f", rp.getMontantTotal()) %> €</td>
                                <td><%= rp.getDateButoire() %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
                <a href="<%= request.getContextPath() %>/user-vol" class="back-btn">
                    <i class="fa-solid fa-arrow-left"></i> Retour à la liste des vols
                </a>
            </div>
        <% } else { %>
            <p>Aucune réservation promotionnelle trouvée.</p>
            <a href="<%= request.getContextPath() %>/user-vol" class="back-btn">
                <i class="fa-solid fa-arrow-left"></i> Retour à la liste des vols
            </a>
        <% } %>
    </div>
</div>
</body>
</html>