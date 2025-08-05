<%@ page import="model.*" %>
<%@ page import="java.util.List" %>
<%
    Vol vol = (Vol) request.getAttribute("vol");
    List<VolSiege> volSieges = (List<VolSiege>) request.getAttribute("volSieges");
    List<PrixVol> prixVols = (List<PrixVol>) request.getAttribute("prixVols");
    String errorMessage = (String) request.getAttribute("errorMessage");
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Details du vol</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="../assets/css/liste.css" rel="stylesheet">
    <link href="../assets/css/sidebar.css" rel="stylesheet">
    <link href="../assets/css/navbar.css" rel="stylesheet">
    <link href="../assets/css/frontoffice.css" rel="stylesheet">
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
            <i class="fa-solid fa-plane header-icon"></i>
            <h1>Details du vol</h1>
        </div>

        <% if (errorMessage != null) { %>
            <div class="error-message" style="color: red; font-size: smaller;">
                <i class="fa-solid fa-exclamation-circle"></i> <%= errorMessage %>
            </div>
        <% } %>

        <% if (vol != null) { %>
            <div class="vol-details">
                <h2>Informations du vol</h2>
                <p><strong>Ville de depart:</strong> <%= vol.getVilleDepart().getNom() %></p>
                <p><strong>Ville d'arrivee:</strong> <%= vol.getVilleArrivee().getNom() %></p>
                <p><strong>Heure de depart:</strong> <%= vol.getDepart() %></p>
                <p><strong>Heure d'arrivee:</strong> <%= vol.getArrivee() %></p>

                <h2>Reserver des places</h2>
                <form id="reservationForm" action="<%= request.getContextPath() %>/user-vol/reserve" method="POST">
                    <input type="hidden" name="volId" value="<%= vol.getId() %>">
                    <input type="hidden" name="utilisateurId" value="<%= user.getId() %>">
                    <table>
                        <thead>
                            <tr>
                                <th>Type de siege</th>
                                <th>Places disponibles</th>
                                <th>Prix (Ar)</th>
                                <th>Nombre a reserver</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (volSieges != null && !volSieges.isEmpty()) { %>
                                <% for (VolSiege volSiege : volSieges) { %>
                                    <% 
                                        PrixVol prixVol = prixVols.stream()
                                            .filter(p -> p.getTypeSiege().getId() == volSiege.getTypeSiege().getId())
                                            .findFirst()
                                            .orElse(null);
                                        double prix = prixVol != null ? prixVol.getPrix() : 0.0;
                                    %>
                                    <tr>
                                        <td><%= volSiege.getTypeSiege().getNom() %></td>
                                        <td><%= volSiege.getNombre() %></td>
                                        <td><%= String.format("%.2f", prix) %></td>
                                        <td>
                                            <input type="number" name="nombre_<%= volSiege.getTypeSiege().getId() %>" 
                                                   min="0" max="<%= volSiege.getNombre() %>" value="0" 
                                                   onchange="updateTotal()">
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="4">Aucune information sur les sièges disponible.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                    <p><strong>Total: </strong><span id="totalPrice">0.00</span> Ar</p>
                    <a href="<%= request.getContextPath() %>/user-vol" class="back-btn">
                        <i class="fa-solid fa-arrow-left"></i> Retour à la liste
                    </a>
                    <button type="button" class="reserve-btn" onclick="submitReservation()">
                        <i class="fa-solid fa-ticket"></i> Reserver
                    </button>
                </form>
            </div>
        <% } else { %>
            <p>Aucun vol trouve.</p>
            <a href="<%= request.getContextPath() %>/user-vol" class="back-btn">
                <i class="fa-solid fa-arrow-left"></i> Retour a la liste
            </a>
        <% } %>
    </div>
</div>

<script>
    function updateTotal() {
        let total = 0;
        <% for (VolSiege volSiege : volSieges) { %>
            <% 
                PrixVol prixVol = prixVols.stream()
                    .filter(p -> p.getTypeSiege().getId() == volSiege.getTypeSiege().getId())
                    .findFirst()
                    .orElse(null);
                double prix = prixVol != null ? prixVol.getPrix() : 0.0;
            %>
            let input_<%= volSiege.getTypeSiege().getId() %> = document.querySelector('input[name="nombre_<%= volSiege.getTypeSiege().getId() %>"]');
            if (input_<%= volSiege.getTypeSiege().getId() %>) {
                let count = parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value) || 0;
                total += count * <%= prix %>;
            }
        <% } %>
        document.getElementById('totalPrice').textContent = total.toFixed(2);
    }

    function submitReservation() {
        let params = [];
        <% for (VolSiege volSiege : volSieges) { %>
            let input_<%= volSiege.getTypeSiege().getId() %> = document.querySelector('input[name="nombre_<%= volSiege.getTypeSiege().getId() %>"]');
            if (input_<%= volSiege.getTypeSiege().getId() %> && parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value) > 0) {
                params.push('<%= volSiege.getTypeSiege().getId() %>:' + parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value));
            }
        <% } %>
        if (params.length === 0) {
            alert("Veuillez selectionner au moins une place à reserver.");
            return;
        }
        let form = document.getElementById('reservationForm');
        let input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'allParams';
        input.value = params.join(',');
        form.appendChild(input);
        form.submit();
    }
</script>
</body>
</html>