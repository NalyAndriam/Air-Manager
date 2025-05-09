<%@ page import="java.util.*" %>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage == null) {
        errorMessage = "Une erreur inattendue s'est produite.";
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Erreur</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="../assets/css/liste.css" rel="stylesheet">
    <link href="../assets/css/sidebar.css" rel="stylesheet">
    <style>
        .main-content { padding: 20px; }
        .container { max-width: 800px; margin: 0 auto; }
        .error-message { color: red; margin-bottom: 15px; font-size: smaller; }
        .header { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
        .submit-btn { 
            background-color: #4CAF50; 
            color: white; 
            padding: 10px 20px; 
            border: none; 
            border-radius: 4px; 
            cursor: pointer; 
            text-decoration: none; 
            display: inline-block; 
        }
        .submit-btn:hover { background-color: #45a049; }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="container">
            <div class="header">
                <i class="fa-solid fa-exclamation-circle header-icon"></i>
                <h1>Erreur</h1>
            </div>

            <div class="error-message">
                <i class="fa-solid fa-exclamation-circle"></i> <%= errorMessage %>
            </div>

            <a href="<%= request.getContextPath() %>/vol" class="submit-btn">
                <i class="fa-solid fa-arrow-left"></i> Retour a la liste des vols
            </a>
        </div>
    </div>
</body>
</html>