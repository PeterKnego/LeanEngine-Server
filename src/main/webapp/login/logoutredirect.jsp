<%@ page import="com.leanengine.server.AuthService" %>
<%@ page import="com.leanengine.server.LeanAccount" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String token = (String) session.getAttribute("lean_token");
    if (token != null) AuthService.resetCurrentAuthData();
    session.removeAttribute("lean_token");

    String redirectUrl;
    if (request.getParameter("redirect") != null) {
        redirectUrl = request.getParameter("redirect");
    } else if (request.getHeader("Referer") != null) {
        redirectUrl = request.getHeader("Referer");
    } else {
        redirectUrl = "/";
    }

    response.sendRedirect(redirectUrl);
%>