<%@ page import="java.util.*" %> 
<%@ page import="mg.emberframework.core.data.*" %> 
<%@ page import="model.user.*" %> 
<%      
    ModelValidationResults result = (ModelValidationResults)request.getAttribute("errors");     
    if (result == null) result = new ModelValidationResults();  
%>   

<!DOCTYPE html> 
<html> 
<head>     
    <title>Login</title>
    <link href="assets/css/login.css" rel="stylesheet">
</head> 
<body>
    <div class="login-container">
        <h2>Login</h2>
        
        <form action="./login" method="post" enctype="multipart/form-data">
            <input type="hidden" name="error-url" value="/login">
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="text" id="email" name="user.email" required value="<%= result.getFieldExceptionValue("user.email") %>">
                <div class="error-message"><%= result.getFieldExceptionMessage("user.email") %></div>
            </div>
            
            <div class="form-group">
                <label for="mdp">Password:</label>
                <input type="password" id="mdp" name="user.mdp" required value="<%= result.getFieldExceptionValue("user.mdp") %>">
                <div class="error-message"><%= result.getFieldExceptionMessage("user.mdp") %></div>
            </div>
            
            <button type="submit" class="submit-btn">Login</button>
            
            <p class="error-message" style="text-align: center; margin-top: 15px;">${errorMessage}</p>
        </form>
        
    </div>
</body> 
</html>