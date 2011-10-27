<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.leanengine.server.LeanEngineSettings" %>
<%@ page import="com.leanengine.server.LeanException" %>
<%@ page import="com.leanengine.server.auth.MobileScheme" %>
<%@ page import="com.leanengine.server.auth.Scheme" %>
<%@ page import="com.leanengine.server.auth.WebScheme" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String type = request.getParameter("type") == null ? "web" : request.getParameter("type");

    // check if OpenID is enabled
    if (!LeanEngineSettings.isOpenIdLoginEnabled()) {
        Scheme scheme;
        if (type.equals("mobile")) {
            scheme = new MobileScheme(request.getServerName());
        } else {
            String hostname = request.getServerName();
            if (request.getLocalPort() != 80 && request.getLocalPort() != 0) {
                hostname = hostname + ":" + request.getLocalPort();
            }
            scheme = new WebScheme(request.getScheme(), hostname);
        }
        response.sendRedirect(scheme.getErrorUrl(new LeanException(LeanException.Error.OpenIdAuthNotEnabled)));
        return;
    }

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
    String redirectUrl;
    if (type.equals("mobile")) {
        redirectUrl = "/login/openid-auth.jsp?next=@mobile";
    } else {
        redirectUrl = request.getParameter("redirect") == null ?
                "/login/openid-auth.jsp?next=/login/logindone.jsp" :
                "/login/openid-auth.jsp?next=" + request.getParameter("redirect");
    }


    String loginUrl = UserServiceFactory.getUserService().createLoginURL(redirectUrl, null, openIdProvider, null);

    response.sendRedirect(loginUrl);
%>