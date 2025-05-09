<%@ page import="model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Vol vol = (Vol) request.getAttribute("vol");
    List<Ville> villes = (List<Ville>) request.getAttribute("villes");
    List<Avion> avions = (List<Avion>) request.getAttribute("avions");
    List<TypeSiege> types = (List<TypeSiege>) request.getAttribute("types");
    List<PrixVol> prixVols = (List<PrixVol>) request.getAttribute("prixVols");
    List<VolSiege> volSieges = (List<VolSiege>) request.getAttribute("volSieges");
    String errorMessage = (String) request.getAttribute("errorMessage");

    // Formatter pour convertir les timestamps en format datetime-local
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    String departureTime = vol != null ? sdf.format(vol.getDepart()) : "";
    String arrivalTime = vol != null ? sdf.format(vol.getArrivee()) : "";
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modification de vol</title>
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
                <h1>Modification du vol # <%= vol.getId() %> </h1>
            </div>

            <% if (errorMessage != null) { %>
                <div class="error-message" style="color: red; font-size: smaller;">
                    <i class="fa-solid fa-exclamation-circle"></i> <%= errorMessage %>
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/vol/detail" method="POST" onsubmit="return prepareFormData()">
                <input type="hidden" name="id" value="<%= vol.getId() %>">

                <div class="form-group">
                    <label for="avion">Avion:</label>
                    <select id="avion" name="avion" required>
                        <% for (Avion avion : avions) { %>
                            <option value="<%= avion.getId() %>" <%= avion.getId() == vol.getAvion().getId() ? "selected" : "" %>>
                                <%= avion.getModele() %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <div class="two-col">
                    <div class="form-group">
                        <label for="departureVille">Ville de depart:</label>
                        <select id="departureVille" name="departureVille" required>
                            <% for (Ville ville : villes) { %>
                                <option value="<%= ville.getId() %>" <%= ville.getId() == vol.getVilleDepart().getId() ? "selected" : "" %>>
                                    <%= ville.getNom() %>
                                </option>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="destinationVille">Ville de destination:</label>
                        <select id="destinationVille" name="destinationVille" required>
                            <% for (Ville ville : villes) { %>
                                <option value="<%= ville.getId() %>" <%= ville.getId() == vol.getVilleArrivee().getId() ? "selected" : "" %>>
                                    <%= ville.getNom() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                </div>

                <div class="two-col">
                    <div class="form-group">
                        <label for="departureTime">Heure de depart:</label>
                        <input type="datetime-local" id="departureTime" name="departureTime" value="<%= departureTime %>" required>
                    </div>

                    <div class="form-group">
                        <label for="arrivalTime">Heure d'arrivee:</label>
                        <input type="datetime-local" id="arrivalTime" name="arrivalTime" value="<%= arrivalTime %>" required>
                    </div>
                </div>

                <h2>Prix et nombre de sieges par type</h2>

                <% for (TypeSiege type : types) { %>
                    <fieldset>
                        <legend><%= type.getNom() %></legend>
                        <input type="hidden" name="typeIds" value="<%= type.getId() %>">
                        <%
                            double prix = 0.0;
                            int nombre = 0;
                            for (PrixVol pv : prixVols) {
                                if (pv.getTypeSiege().getId() == type.getId()) {
                                    prix = pv.getPrix();
                                    break;
                                }
                            }
                            for (VolSiege vs : volSieges) {
                                if (vs.getTypeSiege().getId() == type.getId()) {
                                    nombre = (int) vs.getNombre(); // Cast explicite
                                    break;
                                }
                            }
                        %>
                        <div class="two-col">
                            <div class="form-group">
                                <label>Prix:</label>
                                <input type="number" step="0.01" name="prix" value="<%= prix %>" required>
                            </div>
                            <div class="form-group">
                                <label>Nombre de sieges:</label>
                                <input type="number" name="nombre" value="<%= nombre %>" required>
                            </div>
                        </div>
                    </fieldset>
                <% } %>

                <input type="hidden" id="siegeData" name="siegeData">
                <button type="submit" class="submit-btn">
                    <i class="fa-solid fa-floppy-disk"></i> Enregistrer les modifications
                </button>
            </form>
        </div>
    </div>
</body>
</html>