<%@ page import="model.*" %>
<%@ page import="java.util.List" %>
<%
    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
    List<TypeSiege> types = (List<TypeSiege>) request.getAttribute("types");
    List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
    Integer selectedVolId = (Integer) request.getAttribute("selectedVolId");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des promotions | Air Manager</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="../assets/css/volForm.css" rel="stylesheet">
    <link href="../assets/css/sidebar.css" rel="stylesheet">
    <link href="../assets/css/config.css" rel="stylesheet">
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="container">
            <div class="header">
                <div class="header-icon">
                    <i class="fa-solid fa-tag"></i>
                </div>
                <h1>Gestion des promotions</h1>
                <p class="header-description">Definissez les promotions pour chaque type de siege d'un vol</p>
            </div>

            <% if (errorMessage != null) { %>
                <div class="error-message">
                    <i class="fa-solid fa-exclamation-circle"></i>
                    <span><%= errorMessage %></span>
                </div>
            <% } %>

            <!-- Selection du vol -->
            <form action="<%= request.getContextPath() %>/settings/promotion" method="GET">
                <div class="form-group">
                    <label for="volId">
                        <i class="fa-solid fa-plane"></i>
                        Selectionner un vol:
                    </label>
                    <select id="volId" name="volId" onchange="this.form.submit()" required>
                        <option value="">Choisir un vol</option>
                        <% for (Vol vol : vols) { %>
                            <option value="<%= vol.getId() %>" <%= selectedVolId != null && selectedVolId == vol.getId() ? "selected" : "" %>>
                                <%= vol.getVilleDepart().getNom() %> - <%= vol.getVilleArrivee().getNom() %>
                            </option>
                        <% } %>
                    </select>
                    <small class="form-help">Selectionnez un vol pour afficher ou definir ses promotions</small>
                </div>
            </form>

            <!-- Affichage des promotions actuelles -->
            <% if (selectedVolId != null) { %>
                <div class="current-config">
                    <h2>Promotions actuelles</h2>
                    <% if (promotions != null && !promotions.isEmpty()) { %>
                        <div class="config-details">
                            <% for (Promotion promo : promotions) { %>
                                <p>
                                    <strong><%= promo.getTypeSiege().getNom() %>:</strong>
                                    <span><%= promo.getNombre() %> sieges a <%= promo.getPourcentage() %>% de reduction</span>
                                </p>
                            <% } %>
                        </div>
                    <% } else { %>
                        <div class="no-config">
                            <i class="fa-solid fa-info-circle"></i>
                            <p>Aucune promotion definie pour ce vol. Veuillez remplir le formulaire ci-dessous.</p>
                        </div>
                    <% } %>
                </div>

                <!-- Formulaire d'insertion -->
                <form action="<%= request.getContextPath() %>/settings/promotion" method="POST">
                    <input type="hidden" name="volId" value="<%= selectedVolId %>">
                    <h2 class="form-title">Definir une promotion</h2>

                    <div class="form-group">
                        <label for="typeSiegeId">
                            <i class="fa-solid fa-chair"></i>
                            Type de siege:
                        </label>
                        <select id="typeSiegeId" name="typeSiegeId" required>
                            <option value="">Choisir un type de siege</option>
                            <% for (TypeSiege type : types) { %>
                                <option value="<%= type.getId() %>"><%= type.getNom() %></option>
                            <% } %>
                        </select>
                        <small class="form-help">Selectionnez le type de siege pour la promotion</small>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="nombre">Nombre:</label>
                            <input 
                                type="number" 
                                step="0.01" 
                                id="nombre" 
                                name="nombre" 
                                placeholder="Ex: 10.00"
                                required
                            >
                            <small class="form-help">Nombre de sièges en promotion</small>
                        </div>
                        <div class="form-group">
                            <label for="pourcentage">Pourcentage (%):</label>
                            <input 
                                type="number" 
                                step="0.01" 
                                id="pourcentage" 
                                name="pourcentage" 
                                placeholder="Ex: 25.00"
                                required
                            >
                            <small class="form-help">Pourcentage de reduction</small>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="submit-btn">
                            <i class="fa-solid fa-floppy-disk"></i>
                            Enregistrer la promotion
                        </button>
                    </div>
                </form>
            <% } %>
        </div>
    </div>
</body>
</html>