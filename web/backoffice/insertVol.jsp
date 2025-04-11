<%@ page import="model.*" %> 
<%@ page import="java.util.List" %> 
<%     
    List<Ville> villes = (List<Ville>)request.getAttribute("villes");     
    List<Avion> avions = (List<Avion>)request.getAttribute("avions");     
    List<TypeSiege> types = (List<TypeSiege>)request.getAttribute("types"); 
%> 
<!DOCTYPE html> 
<html lang="fr"> 
<head>     
    <meta charset="UTF-8">     
    <meta name="viewport" content="width=device-width, initial-scale=1.0">     
    <title>Formulaire d'insertion de vol</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="../assets/css/volForm.css" rel="stylesheet">
    <link href="../assets/css/sidebar.css" rel="stylesheet">
    <script>
        function prepareFormData() {
            const typeIds = document.getElementsByName("typeIds");
            const prixInputs = document.getElementsByName("prix");
            const nombreInputs = document.getElementsByName("nombre");
            let data = [];
            for (let i = 0; i < typeIds.length; i++) {
                data.push(typeIds[i].value + ":" + prixInputs[i].value + ":" + nombreInputs[i].value);
            }
            document.getElementById("siegeData").value = data.join(",");
            return true;
        }
    </script>
</head> 
<body>
    <%@ include file="sidebar.jsp" %>
    
    <div class="main-content">
        <div class="container">
            <div class="header">
                <i class="fa-solid fa-plane-departure header-icon"></i>
                <h1>Formulaire d'insertion de vol</h1>
            </div>
            
            <form action="<%= request.getContextPath() %>/vol/insert" method="POST" onsubmit="return prepareFormData()">
                <div class="form-group">
                    <label for="avion">Avion:</label>
                    <select id="avion" name="avion" required>
                        <% for(Avion avion : avions) { %>
                            <option value="<%= avion.getId() %>"><%= avion.getModele() %></option>
                        <% } %>
                    </select>
                </div>
                
                <div class="two-col">
                    <div class="form-group">
                        <label for="departureVille">Ville de depart:</label>
                        <select id="departureVille" name="departureVille" required>
                            <% for(Ville ville : villes) { %>
                                <option value="<%= ville.getId() %>"><%= ville.getNom() %></option>
                            <% } %>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="destinationVille">Ville de destination:</label>
                        <select id="destinationVille" name="destinationVille" required>
                            <% for(Ville ville : villes) { %>
                                <option value="<%= ville.getId() %>"><%= ville.getNom() %></option>
                            <% } %>
                        </select>
                    </div>
                </div>
                
                <div class="two-col">
                    <div class="form-group">
                        <label for="departureTime">Heure de depart:</label>
                        <input type="datetime-local" id="departureTime" name="departureTime" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="arrivalTime">Heure d'arrivee:</label>
                        <input type="datetime-local" id="arrivalTime" name="arrivalTime" required>
                    </div>
                </div>
                
                <h2>Prix et nombre de sieges par type</h2>
                
                <% for(TypeSiege type : types) { %>
                    <fieldset>
                        <legend><%= type.getNom() %></legend>
                        <input type="hidden" name="typeIds" value="<%= type.getId() %>">
                        
                        <div class="two-col">
                            <div class="form-group">
                                <label>Prix:</label>
                                <input type="number" step="0.01" name="prix" required>
                            </div>
                            
                            <div class="form-group">
                                <label>Nombre de sieges:</label>
                                <input type="number" name="nombre" required>
                            </div>
                        </div>
                    </fieldset>
                <% } %>
                
                <input type="hidden" id="siegeData" name="siegeData">
                <button type="submit" class="submit-btn">
                    <i class="fa-solid fa-floppy-disk"></i> Soumettre
                </button>
            </form>
        </div>
    </div>
</body> 
</html>