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
                <form id="reservationForm" action="<%= request.getContextPath() %>/user-vol/reserve" method="POST" enctype="multipart/form-data">
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

                    <div style="margin-top: 1rem;">
                        <label for="passeport"><strong>Upload du passeport:</strong></label>
                        <input type="file" name="passeport" id="passeport" accept="image/*" required>
                    </div>

                    <p><strong>Total: </strong><span id="totalPrice">0.00</span> Ar</p>
                    <a href="<%= request.getContextPath() %>/user-vol" class="back-btn">
                        <i class="fa-solid fa-arrow-left"></i> Retour à la liste
                    </a>
                    <button type="button" class="reserve-btn" onclick="submitReservation()" disabled>
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
        let hasSeats = false;
        <% 
            if (volSieges != null && !volSieges.isEmpty() && prixVols != null) {
                for (VolSiege volSiege : volSieges) {
                    PrixVol prixVol = prixVols.stream()
                        .filter(p -> p.getTypeSiege().getId() == volSiege.getTypeSiege().getId())
                        .findFirst()
                        .orElse(null);
                    if (prixVol != null) {
        %>
                        const input_<%= volSiege.getTypeSiege().getId() %> = document.getElementById('qty_<%= volSiege.getTypeSiege().getId() %>');
                        if (input_<%= volSiege.getTypeSiege().getId() %>) {
                            const qty_<%= volSiege.getTypeSiege().getId() %> = parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value) || 0;
                            if (qty_<%= volSiege.getTypeSiege().getId() %> > 0) {
                                hasSeats = true;
                                total += qty_<%= volSiege.getTypeSiege().getId() %> * <%= prixVol.getPrix() %>;
                            }
                        }
        <% 
                    }
                }
            }
        %>
        document.getElementById('totalPrice').textContent = total.toFixed(2);
        const reserveBtn = document.getElementById('reserveBtn');
        if (reserveBtn) {
            reserveBtn.disabled = !hasSeats;
        }
    }

    function updateSubmitButton(hasSeats) {
        let passeportInput = document.getElementById('passeport');
        let reserveBtn = document.getElementById('reserveBtn');
        let isFileSelected = passeportInput && passeportInput.files && passeportInput.files.length > 0;
        if (reserveBtn) {
            reserveBtn.disabled = !(hasSeats && isFileSelected);
        }
    }

    function submitReservation() {
        let form = document.getElementById('reservationForm');
        let passeportInput = document.getElementById('passeport');
        let params = [];

        <% 
            if (volSieges != null && !volSieges.isEmpty()) {
                for (VolSiege volSiege : volSieges) {
        %>
                    const input_<%= volSiege.getTypeSiege().getId() %> = document.querySelector('input[name="nombre_<%= volSiege.getTypeSiege().getId() %>"]');
                    if (input_<%= volSiege.getTypeSiege().getId() %> && parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value) > 0) {
                        params.push('<%= volSiege.getTypeSiege().getId() %>:' + parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value));
                    }
        <% 
                }
            }
        %>

        if (params.length === 0) {
            alert("Veuillez sélectionner au moins une place à réserver.");
            return;
        }
        if (!passeportInput || !passeportInput.files || passeportInput.files.length === 0) {
            alert("Veuillez téléverser une image de votre passeport.");
            return;
        }

        let allParamsInput = document.querySelector('input[name="allParams"]');
        if (!allParamsInput) {
            allParamsInput = document.createElement('input');
            allParamsInput.type = 'hidden';
            allParamsInput.name = 'allParams';
            form.appendChild(allParamsInput);
        }
        allParamsInput.value = params.join(',');

        form.submit();
    }

    // Ajouter des écouteurs pour activer/désactiver le bouton
    const passeportInput = document.getElementById('passeport');
    if (passeportInput) {
        passeportInput.addEventListener('change', function() {
            updateTotal();
        });
    }

    // Appeler updateTotal au chargement pour initialiser l'état du bouton
    document.addEventListener('DOMContentLoaded', updateTotal);
</script>
</body>
</html>