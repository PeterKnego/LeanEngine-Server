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
    <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js" type="text/javascript"></script>
</head>
<%
    LeanAccount account = AuthService.getCurrentAccount();
    String show = request.getParameter("show") == null ? "entities" : request.getParameter("show");
    boolean isLoginError = request.getParameter("errorlogin") != null
            && request.getParameter("errorlogin").equals("true") ? true : false;

%>
<body style="padding-top: 42px;">

<div class="topbar">
    <div class="topbar-inner">
        <div class="container">
            <a class="brand" href="#">LeanEngine Demo</a>
            <%--<ul class="nav">--%>
            <%--<li class="active"><a href="#overview">Entities</a></li>--%>
            <%--<li><a href="#about">About</a></li>--%>
            <%--<li><a href="#grid-system">Grid</a></li>--%>
            <%--</ul>--%>
            <%
                if (account == null) {
            %>
            <div class="pull-right">
                <ul class="nav">
                    <li><a>Login with</a></li>
                    <li>
                        <a href='/login/facebook-start.jsp?redirect=<%= request.getRequestURI()%>'>
                            <img height="24px" width="24px" src="images/facebook.png" alt="Facebook login"/>
                        </a>
                    </li>
                    <li>
                        <a href='/login/openid-start.jsp?redirect=/home.jsp&provider=google'>
                            <img height="24px" width="24px" src="images/google.png" alt="Google login"/>
                        </a>
                    </li>
                    <li>
                        <a href='/login/openid-start.jsp?redirect=<%= request.getRequestURI()%>&provider=yahoo'>
                            <img height="24px" width="24px" src="images/yahoo.png" alt="Yahoo login"/>
                        </a>
                    </li>
                </ul>
            </div>
            <%
            } else {
            %>
            <div class="pull-right">
                <ul class="nav">
                    <li><a href='/login/logoutredirect.jsp'>Logout</a></li>
                    <li><a href='/login/logoutredirect.jsp'>
                        <img height="24px" width="24px" src="images/exit.png" alt="Logout"/>
                    </a></li>
                </ul>
            </div>
            <%
                }
            %>
        </div>
    </div>
</div>

<div class="container">
    <div class="row">
        <div class="span16">
            <%
                if (account != null) {
            %>

            <% if (show.equals("entities")) {%>
            <br/><br/>
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
        </div>
    </div>
</div>

</body>
</html>
