<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.leanengine.server.AuthService" %>
<%@ page import="com.leanengine.server.LeanAccount" %>
<%@ page import="com.leanengine.server.appengine.DatastoreUtils" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>LeanEngine Demo</title>
    <link href="style.css" rel="stylesheet" type="text/css"/>
</head>

<%
    LeanAccount account = AuthService.getCurrentAccount();
    String show = request.getParameter("show") == null ? "entities" : request.getParameter("show");
%>
<body>
<h1 class="Header">LeanEngine Demo</h1>
<%
    if (account == null) {

%>
<p>
    Login with&nbsp
    <a href='/login/facebook-start.jsp?redirect=<%= request.getRequestURI()%>'><img src="images/facebook.png" alt="Facebook login"/></a>
    <a href='/login/openid-start.jsp?redirect=<%= request.getRequestURI()%>&provider=google'><img src="images/google.png" alt="Google login"/></a>
    <a href='/login/openid-start.jsp?redirect=<%= request.getRequestURI()%>&provider=yahoo'><img src="images/yahoo.png" alt="Yahoo login"/></a>
</p>
<hr/>
<%
} else {
%>
<p>
    <a href='/login/logoutredirect.jsp'><img src="images/exit.png" alt="Logout"/></a>
</p>
<hr/>
<%--<p>--%>
<%--<a href='<%= request.getRequestURI()+"?show=users"%>'><img src="images/UsersButton.png"/></a>--%>
<%--<a href='<%= request.getRequestURI()+"?show=entities"%>'><img src="images/EntitiesButton.png"/></a>--%>
<%--</p>--%>

<% if (show.equals("entities")) {%>
<table class="sample" width="80%" border="0" cellpadding="2">
    <tr>
        <th colspan="3">
            <div align="left"><span>Entities for user: <%=account.nickName%></span></div>
        </th>
    </tr>
    <tr>
        <th>
            <div align="left"><span>Name</span></div>
        </th>
        <th>
            <div align="left"><span>User</span></div>
        </th>
        <th>
            <div align="left"><span>Properties</span></div>
        </th>
    </tr>
    <%
        List<Entity> entities = DatastoreUtils.getPrivateEntities();

        // protection - does nothing just prevents NPE
        if (entities == null) entities = new ArrayList<Entity>();

        for (Entity entity : entities) {
            StringBuilder props = new StringBuilder();
            String separator = "";
            for (Map.Entry<String, Object> prop : entity.getProperties().entrySet()) {
                if (prop.getKey().startsWith("_")) continue;
                props.append(separator).append(prop.getKey()).append(":").append(prop.getValue());
                separator = ", ";
            }
    %>
    <tr>
        <td><%=entity.getProperty("_entity")%>
        </td>
        <td><%=entity.getProperty("_user")%>
        </td>
        <td><%=props.toString()%>
        </td>
    </tr>
    <%
        }
    %>
</table>
<%
        }
    }
%>

</body>
</html>
