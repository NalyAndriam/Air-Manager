<%@ page import="model.*" %>
<%@ page import="java.util.List" %>
<%
    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
    List<Ville> villes = (List<Ville>) request.getAttribute("villes");
    String errorMessage = (String) request.getAttribute("errorMessage");
    Utilisateur user = (Utilisateur) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des vols</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="./assets/css/liste.css" rel="stylesheet">
    <link href="./assets/css/sidebar.css" rel="stylesheet">
    <link href="./assets/css/navbar.css" rel="stylesheet">
    <style>
        .vol-table tr {
            cursor: pointer;
        }
        .vol-table tr:hover {
            background-color: #f5f5f5;
        }
    </style>
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
                <i class="fa-solid fa-list header-icon"></i>
                <h1>Liste des vols</h1>
            </div>
            
            <% if (errorMessage != null) { %>
                <div class="error-message" style="color: red; font-size: smaller;">
                    <i class="fa-solid fa-exclamation-circle"></i> <%= errorMessage %>
                </div>
            <% } %>
            
            <div class="search-section">
                <h2><i class="fa-solid fa-search"></i> Rechercher un vol</h2>
                <form action="<%= request.getContextPath() %>/user-vol" method="GET" class="search-form">
                    <div class="search-grid">
                        <div class="search-item">
                            <label for="villeDepart">Ville de départ:</label>
                            <select id="villeDepart" name="villeDepart">
                                <option value="0">Sélectionner une ville de départ</option>
                                <% if (villes != null && !villes.isEmpty()) { %>
                                    <% for (Ville ville : villes) { %>
                                        <option value="<%= ville.getId() %>"><%= ville.getNom() %></option>
                                    <% } %>
                                <% } %>
                            </select>
                        </div>
                        <div class="search-item">
                            <label for="villeArrivee">Ville d'arrivée:</label>
                            <select id="villeArrivee" name="villeArrivee">
                                <option value="0">Sélectionner une ville d'arrivée</option>
                                <% if (villes != null && !villes.isEmpty()) { %>
                                    <% for (Ville ville : villes) { %>
                                        <option value="<%= ville.getId() %>"><%= ville.getNom() %></option>
                                    <% } %>
                                <% } %>
                            </select>
                        </div>
                        <div class="search-item">
                            <label for="dateDepart">Date de départ</label>
                            <input type="date" id="dateDepart" name="dateDepart">
                        </div>
                        <div class="search-item">
                            <label for="dateArrivee">Date d'arrivée</label>
                            <input type="date" id="dateArrivee" name="dateArrivee">
                        </div>
                        <div class="search-item">
                            <label for="prixMin">Prix minimum</label>
                            <input type="number" id="prixMin" name="prixMin" placeholder="Saisir un prix" step="0.01">
                        </div>
                        <div class="search-item">
                            <label for="prixMax">Prix maximum</label>
                            <input type="number" id="prixMax" name="prixMax" placeholder="Saisir un prix" step="0.01">
                        </div>
                    </div>
                    <div class="search-actions">
                        <button type="submit" class="search-btn">
                            <i class="fa-solid fa-search"></i> Rechercher
                        </button>
                        <button type="reset" class="reset-btn">
                            <i class="fa-solid fa-times"></i> Réinitialiser
                        </button>
                    </div>
                </form>
            </div>
            
            <table class="vol-table">
                <thead>
                    <tr>
                        <th>Ville de départ</th>
                        <th>Ville d'arrivée</th>
                        <th>Heure de départ</th>
                        <th>Heure d'arrivée</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (vols != null && !vols.isEmpty()) { %>
                        <% for (Vol vol : vols) { %>
                            <tr onclick="window.location='<%= request.getContextPath() %>/user-vol/details?volId=<%= vol.getId() %>'">
                                <td><%= vol.getVilleDepart().getNom() %></td>
                                <td><%= vol.getVilleArrivee().getNom() %></td>
                                <td><%= vol.getDepart() %></td>
                                <td><%= vol.getArrivee() %></td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="4">Aucun vol trouvé.</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>