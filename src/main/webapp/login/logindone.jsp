<%@ page import="com.leanengine.server.auth.AuthService" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.leanengine.server.auth.LeanAccount" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Login done</title></head>
<body>
<% LeanAccount account = AuthService.getCurrentAccount(); %>
User <%=account != null ? account.nickName : "N/A"%> logged in successfully.

<br/><br/>
Session attributes:
<br/>
<%
    Enumeration attrNames = session.getAttributeNames();
    while (attrNames.hasMoreElements()) {
        String attrName = (String) attrNames.nextElement();
        String attr = (String) session.getAttribute(attrName);
%>
<%=attrName%>:<%=attr%>
<br/>
<%
    }
%>
</body>
</html>