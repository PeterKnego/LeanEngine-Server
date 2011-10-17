<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String type = request.getParameter("type") == null ? "web" : request.getParameter("type");

    // default OpenID provider is Google
    String openIdProvider = request.getParameter("provider");

    // is it a shortcut?
    if (openIdProvider == null || openIdProvider.equals("google")) {
        openIdProvider = "https://www.google.com/accounts/o8/id";
    } else if (openIdProvider.equals("yahoo")) {
        openIdProvider = "https://me.yahoo.com";
    }

    // redirectUrl is composed so that it redirects twice:
    // first to /login/openid-auth.jsp for authentication
    // second to the final destination URL
    String redirectUrl = request.getParameter("redirect") == null ?
            "/login/openid-auth.jsp?&next=/login/logindone.jsp&type="+type :
            "/login/openid-auth.jsp?next="+request.getParameter("redirect")+"&type="+type;

    String loginUrl = UserServiceFactory.getUserService().createLoginURL(redirectUrl, null, openIdProvider, null);

    response.sendRedirect(loginUrl);
%>