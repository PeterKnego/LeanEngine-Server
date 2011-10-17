<%@ page import="com.leanengine.server.AuthToken" %>
<%@ page import="com.leanengine.server.Scheme" %>
<%@ page import="com.leanengine.server.MobileScheme" %>
<%@ page import="com.leanengine.server.WebScheme" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String type = request.getParameter("type") == null ? "web" : request.getParameter("type");

    // type parameters tells us the type of redirect we should perform
    Scheme scheme;
    if (type.equals("mobile")) {
        scheme = new MobileScheme(request.getServerName());
    } else {
        scheme = new WebScheme(request.getScheme(), request.getServerName());
    }

    // get user


    // get toke for this user
    AuthToken lean_token = null;       //todo null

    // save token in session
    session.setAttribute("lean_token", lean_token.token);



    //send lean_token back to browser
    response.sendRedirect(scheme.getUrl(lean_token.token, null));       // todo null
%>