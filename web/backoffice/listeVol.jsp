<%@ page import="model.*" %>
<%@ page import="java.util.List" %>
<%
    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
    List<Ville> villes = (List<Ville>) request.getAttribute("villes");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des vols</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="./assets/css/liste.css" rel="stylesheet">
    <link href="./assets/css/sidebar.css" rel="stylesheet">
</head>
<body>
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
                <form action="<%= request.getContextPath() %>/vol" method="GET" class="search-form">
                    <div class="search-grid">
                        <div class="search-item">
                            <label for="villeDepart">Ville de depart:</label>
                            <select id="villeDepart" name="villeDepart">
                                <option value="0">Selectionner une ville de depart</option>
                                <% if (villes != null && !villes.isEmpty()) { %>
                                    <% for (Ville ville : villes) { %>
                                        <option value="<%= ville.getId() %>"><%= ville.getNom() %></option>
                                    <% } %>
                                <% } %>
                            </select>
                        </div>
                        <div class="search-item">
                            <label for="villeArrivee">Ville d'arrivee:</label>
                            <select id="villeArrivee" name="villeArrivee">
                                <option value="0">Selectionner une ville d'arrivee</option>
                                <% if (villes != null && !villes.isEmpty()) { %>
                                    <% for (Ville ville : villes) { %>
                                        <option value="<%= ville.getId() %>"><%= ville.getNom() %></option>
                                    <% } %>
                                <% } %>
                            </select>
                        </div>
                        <div class="search-item">
                            <label for="dateDepart">Date de depart</label>
                            <input type="date" id="dateDepart" name="dateDepart">
                        </div>
                        <div class="search-item">
                            <label for="dateArrivee">Date d'arrivee</label>
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
                            <i class="fa-solid fa-times"></i> Reinitialiser
                        </button>
                    </div>
                </form>
            </div>
            
            <table class="vol-table">
                <thead>
                    <tr>
                        <th>Ville de depart</th>
                        <th>Ville d'arrivee</th>
                        <th>Heure de depart</th>
                        <th>Heure d'arrivee</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (vols != null && !vols.isEmpty()) { %>
                        <% for (Vol vol : vols) { %>
                            <tr>
                                <td><%= vol.getVilleDepart().getNom() %></td>
                                <td><%= vol.getVilleArrivee().getNom() %></td>
                                <td><%= vol.getDepart() %></td>
                                <td><%= vol.getArrivee() %></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/vol/detail?id=<%= vol.getId() %>" class="action-btn">
                                        <i class="fa-solid fa-edit"></i> Modifier
                                    </a>
                                    <a href="<%= request.getContextPath() %>/vol/delete?id=<%= vol.getId() %>" class="action-btn" onclick="return confirm('Supprimer ce vol ?')">
                                        <i class="fa-solid fa-trash"></i> Supprimer
                                    </a>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="5">Aucun vol trouve.</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>