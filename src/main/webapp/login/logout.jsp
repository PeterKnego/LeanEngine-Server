<%@ page import="com.leanengine.server.AuthService" %>
<%@ page import="com.leanengine.server.LeanAccount" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Logout</title></head>
<body>
<%
    LeanAccount user = AuthService.getCurrentAccount();
    String token = (String) session.getAttribute("lean_token");
    if(token!=null) AuthService.resetCurrentAuthData();
    session.removeAttribute("lean_token");

%>
User <%=user != null ? user.nickName : "N/A"%> logged out.
</body>
</html>