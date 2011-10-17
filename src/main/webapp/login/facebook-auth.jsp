<%@ page import="com.leanengine.server.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // user is trying to login
    // reset the existing auth data if it exists
    AuthService.resetCurrentAuthData();
    session.removeAttribute("lean_token");

    String state = request.getParameter("state");
    if (state == null || !state.equals(session.getAttribute("antiCSRF"))) {
        // oauth error - redirect back to client with error
        response.sendRedirect(new WebScheme(request.getScheme(), request.getScheme()).getErrorUrl(1,
                "CSRF protection code missing."));
        return;
    }

    String redirectUrl = null;

    // 'state' parameter has format "login_type:token:redirect_url"
    // extract the login type from 'state' parameter
    Scheme scheme;
    if (state.startsWith("mob:")) {
        scheme = new MobileScheme(request.getServerName());
    } else {
        scheme = new WebScheme(request.getScheme(), request.getServerName());

        // extract the redirect URL
        String[] stateItems = state.split(":");
        redirectUrl = (stateItems.length == 3) ? stateItems[2] : null;
    }

    String error = request.getParameter("error");
    String code = request.getParameter("code");
    if (error != null) {
        // oauth error - redirect back to client with error
        response.sendRedirect(scheme.getErrorUrl(2, "Facebook OAuth error: "+error));

    } else if (code != null) {

        String currentUrl = request.getRequestURL().toString();

        try {
            // authenticate with Facebook Graph OAuth API
            AuthToken lean_token = FacebookAuth.graphAuthenticate(currentUrl, code);

            // save token in session
            session.setAttribute("lean_token", lean_token.token);

            //send lean_token back to browser
            response.sendRedirect(scheme.getUrl(lean_token.token, redirectUrl));

        } catch (LeanException le) {
            response.sendRedirect(scheme.getErrorUrl(le.getErrorCode(), le.getMessage()));
        }
    } else {
        // error: this should not happen - redirect back to client with error
        response.sendRedirect(scheme.getErrorUrl(3, "Facebook OAuth error: required parameters missing."));

    }

%>
