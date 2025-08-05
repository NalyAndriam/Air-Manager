<%@ page import="model.*" %>
<%@ page import="java.util.List" %>
<%
    ReservationConfig configuration = (ReservationConfig) request.getAttribute("config");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Configuration de reservation | Air Manager</title>
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
                    <i class="fa-solid fa-cog"></i>
                </div>
                <h1>Configuration de reservation</h1>
                <p class="header-description">Definissez les delais de reservation et d'annulation pour les vols</p>
            </div>
            
            <% if (errorMessage != null) { %>
                <div class="error-message">
                    <i class="fa-solid fa-exclamation-circle"></i>
                    <span><%= errorMessage %></span>
                </div>
            <% } %>
            
            <!-- Affichage de la configuration actuelle -->
            <div class="current-config">
                <h2>Configuration actuelle</h2>
                <% if (configuration != null) { %>
                    <div class="config-details">
                        <p>
                            <strong>Heure de reservation:</strong>
                            <span><%= configuration.getHeureReservation() %> heures avant le depart</span>
                        </p>
                        <p>
                            <strong>Heure d'annulation:</strong>
                            <span><%= configuration.getHeureAnnulation() %> heures avant le depart</span>
                        </p>
                    </div>
                <% } else { %>
                    <div class="no-config">
                        <i class="fa-solid fa-info-circle"></i>
                        <p>Aucune configuration definie. Veuillez remplir le formulaire ci-dessous.</p>
                    </div>
                <% } %>
            </div>
            
            <!-- Formulaire d'insertion -->
            <form action="<%= request.getContextPath() %>/settings/reservation" method="POST">
                <h2 class="form-title">Modifier la configuration</h2>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="heureReservation">
                            <i class="fa-regular fa-clock"></i>
                            Heure de reservation (heures):
                        </label>
                        <input 
                            type="number" 
                            step="0.01" 
                            id="heureReservation" 
                            name="heureReservation" 
                            placeholder="Ex: 24.00"
                            value="<%= configuration != null ? configuration.getHeureReservation() : "" %>"
                            required
                        >
                        <small class="form-help">Temps minimum avant le vol pour effectuer une reservation</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="heureAnnulation">
                            <i class="fa-solid fa-ban"></i>
                            Heure d'annulation (heures):
                        </label>
                        <input 
                            type="number" 
                            step="0.01" 
                            id="heureAnnulation" 
                            name="heureAnnulation" 
                            placeholder="Ex: 12.00"
                            value="<%= configuration != null ? configuration.getHeureAnnulation() : "" %>"
                            required
                        >
                        <small class="form-help">Temps minimum avant le vol pour annuler une reservation</small>
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="submit-btn">
                        <i class="fa-solid fa-floppy-disk"></i>
                        Enregistrer les modifications
                    </button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>