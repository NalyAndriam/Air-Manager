<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.sql.Connection" %>
<%
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    Connection conn = null;
    List<Reservation> reservations = null;
    try {
        conn = Database.getConnection();
        reservations = Reservation.getByUtilisateurId(conn, user.getId());
    } catch (Exception e) {
        request.setAttribute("errorMessage", "Erreur lors de la récupération des réservations: " + e.getMessage());
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
    <title>Mes Réservations</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="./assets/css/liste.css" rel="stylesheet">
    <link href="./assets/css/sidebar.css" rel="stylesheet">
    <link href="./assets/css/navbar.css" rel="stylesheet">
    <link href="./assets/css/frontoffice.css" rel="stylesheet">
    <!-- jsPDF & html2canvas CDN -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
</head>
<body>
<div class="navbar">
    <div class="user-info">
        <i class="fa-solid fa-user"></i>
        <%= user.getNom() %>
    </div>
</div>

<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <div class="container">
        <div class="header">
            <i class="fa-solid fa-ticket header-icon"></i>
            <h1>Mes Réservations</h1>
        </div>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message" style="color: red; font-size: smaller;">
                <i class="fa-solid fa-exclamation-circle"></i> <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        <% if (request.getAttribute("successMessage") != null) { %>
            <div class="success-message" style="color: green; font-size: smaller;">
                <i class="fa-solid fa-check-circle"></i> <%= request.getAttribute("successMessage") %>
            </div>
        <% } %>

        <% if (reservations != null && !reservations.isEmpty()) { %>
            <div class="reservations-list">
                <table>
                    <thead>
                        <tr>
                            <th>Ville de départ</th>
                            <th>Ville d'arrivée</th>
                            <th>Date de départ</th>
                            <th>Date d'arrivée</th>
                            <th>Types de sièges</th>
                            <th>Nombre de places</th>
                            <th>Date de réservation</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            conn = Database.getConnection();
                            for (Reservation reservation : reservations) {
                                boolean isPaid = false;
                                try {
                                    isPaid = reservation.estPaye(conn);
                                } catch (Exception e) {
                                    request.setAttribute("errorMessage", "Erreur lors de la vérification du paiement: " + e.getMessage());
                                }
                        %>
                            <tr>
                                <td><%= reservation.getVol().getVilleDepart().getNom() %></td>
                                <td><%= reservation.getVol().getVilleArrivee().getNom() %></td>
                                <td><%= reservation.getVol().getDepart() %></td>
                                <td><%= reservation.getVol().getArrivee() %></td>
                                <td>
                                    <ul>
                                        <% 
                                            List<TypeSiege> typeSieges = reservation.getTypeSieges();
                                            for (TypeSiege t : typeSieges) {
                                        %>
                                            <li><%= t.getNom() %></li>
                                        <% } %>
                                    </ul>
                                </td>
                                <td>
                                    <ul>
                                        <% 
                                            List<Integer> nombres = reservation.getNombres();
                                            for (Integer n : nombres) {
                                        %>
                                            <li><%= n %></li>
                                        <% } %>
                                    </ul>
                                </td>
                                <td><%= reservation.getDate() %></td>
                                <td>
                                    <button onclick="generatePDF(
                                        '<%= user.getNom() %>',
                                        '<%= reservation.getVol().getVilleDepart().getNom() %>',
                                        '<%= reservation.getVol().getVilleArrivee().getNom() %>',
                                        '<%= reservation.getVol().getDepart() %>',
                                        '<%= reservation.getVol().getArrivee() %>',
                                        '<%= String.join(", ", reservation.getTypeSieges().stream().map(TypeSiege::getNom).toList()) %>',
                                        '<%= reservation.getNombres().toString().replaceAll("[\\[\\]]", "") %>',
                                        '<%= reservation.getDate() %>',
                                        '<%= reservation.getPasseport() != null ? Base64.getEncoder().encodeToString(reservation.getPasseport()) : "" %>'
                                    )" class="generate-btn">
                                        <i class="fa-solid fa-file-pdf"></i> 
                                    </button>
                                    <form action="<%= request.getContextPath() %>/user-vol/cancel" method="POST" style="display:inline;" onsubmit="return confirm('Voulez-vous vraiment annuler cette réservation ?');">
                                        <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                                        <button type="submit" class="cancel-btn">
                                            <i class="fa-solid fa-times"></i> Annuler
                                        </button>
                                    </form>
                                    <% if (!isPaid) { %>
                                        <form action="<%= request.getContextPath() %>/user-resa/pay" method="POST" style="display:inline;" onsubmit="return confirm('Voulez-vous vraiment effectuer le paiement pour cette réservation ?');">
                                            <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                                            <button type="submit" class="pay-btn">
                                                <i class="fa-solid fa-credit-card"></i> Payer
                                            </button>
                                        </form>
                                    <% } %>
                                </td>
                            </tr>
                        <% 
                            }
                            if (conn != null) {
                                try {
                                    conn.close();
                                } catch (Exception e) {
                                    request.setAttribute("errorMessage", "Erreur lors de la fermeture de la connexion: " + e.getMessage());
                                }
                            }
                        %>
                    </tbody>
                </table>

                <!-- Section modèle billet (invisible à l'écran, servira pour le PDF) -->
                <div id="ticket-template" style="display:none; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; width: 700px; background-color: #fff; border-radius: 0.5rem; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);">
                    <!-- Header -->
                    <div style="background-color: #f3f4f6; color: #374151; padding: 1.5rem; border-bottom: 1px solid #e5e7eb;">
                        <div style="display: flex; align-items: center; justify-content: space-between;">
                            <div>
                                <h1 style="margin: 0; font-size: 1.5rem; font-weight: 600; color: #1f2937;">Air-Manager</h1>
                                <p style="margin: 0; font-size: 0.875rem; color: #4b5563;">Confirmation de Réservation</p>
                            </div>
                        </div>
                    </div>

                    <!-- Contenu principal -->
                    <div style="padding: 1.5rem;">
                        <!-- Informations passager -->
                        <div style="background-color: #fff; border: 1px solid #e5e7eb; border-radius: 0.375rem; padding: 1rem; margin-bottom: 1rem;">
                            <h3 style="margin: 0 0 0.75rem 0; color: #374151; font-size: 1rem; font-weight: 600; text-transform: uppercase;">Informations Passager</h3>
                            <p style="margin: 0; font-size: 1.25rem; font-weight: 600; color: #1f2937;"><span id="pdf-nom"></span></p>
                        </div>

                        <!-- Détails du vol -->
                        <div style="background-color: #fff; border: 1px solid #e5e7eb; border-radius: 0.375rem; padding: 1rem; margin-bottom: 1rem;">
                            <h3 style="margin: 0 0 0.75rem 0; color: #374151; font-size: 1rem; font-weight: 600; text-transform: uppercase;">Détails du Vol</h3>
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <div style="text-align: center; flex: 1;">
                                    <p style="margin: 0 0 0.25rem 0; font-size: 0.75rem; color: #4b5563; text-transform: uppercase; font-weight: 600;">Départ</p>
                                    <p style="margin: 0; font-size: 1.125rem; font-weight: 600; color: #1f2937;"><span id="pdf-depart"></span></p>
                                    <p style="margin: 0.25rem 0 0 0; font-size: 0.875rem; color: #4b5563;"><span id="pdf-date-depart"></span></p>
                                </div>
                                <div style="flex: 0 0 50px; text-align: center;">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#6c757d" stroke-width="2">
                                        <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
                                    </svg>
                                </div>
                                <div style="text-align: center; flex: 1;">
                                    <p style="margin: 0 0 0.25rem 0; font-size: 0.75rem; color: #4b5563; text-transform: uppercase; font-weight: 600;">Arrivée</p>
                                    <p style="margin: 0; font-size: 1.125rem; font-weight: 600; color: #1f2937;"><span id="pdf-arrivee"></span></p>
                                    <p style="margin: 0.25rem 0 0 0; font-size: 0.875rem; color: #4b5563;"><span id="pdf-date-arrivee"></span></p>
                                </div>
                            </div>
                        </div>

                        <!-- Informations de réservation -->
                        <div style="display: flex; gap: 1rem; margin-bottom: 1rem;">
                            <div style="background-color: #fff; border: 1px solid #e5e7eb; border-radius: 0.375rem; padding: 1rem; flex: 1;">
                                <h4 style="margin: 0 0 0.5rem 0; color: #374151; font-size: 0.75rem; font-weight: 600; text-transform: uppercase;">Types de Sièges</h4>
                                <p style="margin: 0; font-size: 1rem; font-weight: 600; color: #1f2937;"><span id="pdf-sieges"></span></p>
                            </div>
                            <div style="background-color: #fff; border: 1px solid #e5e7eb; border-radius: 0.375rem; padding: 1rem; flex: 1;">
                                <h4 style="margin: 0 0 0.5rem 0; color: #374151; font-size: 0.75rem; font-weight: 600; text-transform: uppercase;">Nombre de Places</h4>
                                <p style="margin: 0; font-size: 1rem; font-weight: 600; color: #1f2937;"><span id="pdf-places"></span></p>
                            </div>
                        </div>

                        <!-- Image du passeport -->
                        <div style="background-color: #fff; border: 1px solid #e5e7eb; border-radius: 0.375rem; padding: 1rem; margin-bottom: 1rem;">
                            <h4 style="margin: 0 0 0.5rem 0; color: #374151; font-size: 0.75rem; font-weight: 600; text-transform: uppercase;">Passeport</h4>
                            <img id="pdf-passeport" style="max-width: 200px; height: auto;" src="" alt="Passeport">
                        </div>

                        <!-- Date de réservation -->
                        <div style="background-color: #fff; border: 1px solid #e5e7eb; border-radius: 0.375rem; padding: 1rem;">
                            <h4 style="margin: 0 0 0.5rem 0; color: #374151; font-size: 0.75rem; font-weight: 600; text-transform: uppercase;">Date de Réservation</h4>
                            <p style="margin: 0; font-size: 1rem; font-weight: 600; color: #1f2937;"><span id="pdf-date-reservation"></span></p>
                        </div>

                        <!-- Footer -->
                        <div style="margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #e5e7eb; text-align: center;">
                            <p style="margin: 0; font-size: 0.875rem; color: #4b5563;">Merci de voyager avec Air-Manager</p>
                        </div>
                    </div>
                </div>
            </div>
            <a href="<%= request.getContextPath() %>/user-vol" class="back-btn">
                <i class="fa-solid fa-arrow-left"></i> Retour à la liste des vols
            </a>
        <% } else { %>
            <p>Aucune réservation trouvée.</p>
            <a href="<%= request.getContextPath() %>/user-vol" class="back-btn">
                <i class="fa-solid fa-arrow-left"></i> Retour à la liste des vols
            </a>
        <% } %>
    </div>
</div>

<script>
    async function generatePDF(nom, depart, arrivee, dateDepart, dateArrivee, sieges, places, dateResa, passeportBase64) {
        document.getElementById("pdf-nom").textContent = nom;
        document.getElementById("pdf-depart").textContent = depart;
        document.getElementById("pdf-arrivee").textContent = arrivee;
        document.getElementById("pdf-date-depart").textContent = formatDate(dateDepart);
        document.getElementById("pdf-date-arrivee").textContent = formatDate(dateArrivee);
        document.getElementById("pdf-sieges").textContent = sieges;
        document.getElementById("pdf-places").textContent = places;
        document.getElementById("pdf-date-reservation").textContent = formatDate(dateResa);
        const passeportImg = document.getElementById("pdf-passeport");
        if (passeportBase64) {
            passeportImg.src = "data:image/png;base64," + passeportBase64;
        } else {
            passeportImg.style.display = "none";
        }

        const element = document.getElementById("ticket-template");
        element.style.display = "block";

        const canvas = await html2canvas(element);
        const imgData = canvas.toDataURL('image/png');
        const { jsPDF } = window.jspdf;
        const pdf = new jsPDF();
        pdf.addImage(imgData, 'PNG', 10, 10, 190, 0);
        pdf.save("reservation.pdf");

        element.style.display = "none";
    }

    function formatDate(dateStr) {
        const date = new Date(dateStr);
        const options = { day: '2-digit', month: '2-digit', year: 'numeric', hour: "2-digit", minute: "2-digit" };
        return date.toLocaleString('fr-FR', options);
    }
</script>

<style>
    .pay-btn {
        background-color: #28a745;
        color: white;
        padding: 0.5rem 1rem;
        border: none;
        border-radius: 0.375rem;
        cursor: pointer;
        font-size: 0.9rem;
        margin-left: 0.5rem;
        transition: background-color 0.2s;
    }
    .pay-btn:hover {
        background-color: #218838;
    }
    .pay-btn i {
        margin-right: 0.3rem;
    }
</style>
</body>
</html>