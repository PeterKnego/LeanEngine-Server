<%@ page import="com.leanengine.server.*" %>
<%@ page import="java.util.UUID" %>
<%@ page import="com.leanengine.server.auth.Scheme" %>
<%@ page import="com.leanengine.server.auth.WebScheme" %>
<%@ page import="com.leanengine.server.auth.MobileScheme" %>
<%@ page import="com.leanengine.server.auth.FacebookAuth" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String type = request.getParameter("type");
    String display = request.getParameter("display");
    Scheme scheme;
    String facebookAntiCSRF;

    // checking if request is coming from mobile client
    boolean isMobile = type != null && type.equals("mobile");

    String redirectUrl = request.getParameter("redirect") == null ? "" : request.getParameter("redirect");

    if (isMobile) {
        // login via mobile device - this means redirecting to 'leanengine://hostname/?access_token=..' URLs
        scheme = new MobileScheme(request.getServerName());
        // we add 'mob:' in front of state parameter to indicate that request comes from mobile device
        facebookAntiCSRF = "mob:" + UUID.randomUUID().toString();
    } else {
        // login via web interface
        scheme = new WebScheme(request.getScheme(), request.getServerName());
        // indicating request comes from browser
        facebookAntiCSRF = "web:" + UUID.randomUUID().toString() + ":" + redirectUrl;
    }

    // Facebook login is not enabled in settings
    if (!LeanEngineSettings.isFacebookLoginEnabled()) {
        response.sendRedirect(scheme.getErrorUrl(new LeanException(LeanException.Error.FacebookAuthNotEnabled)));
        return;
    }

    // 'state' parameter is passed around the Facebook OAuth redirects and ends up at our final auth page
    // Primarily it's used for preventing CSRF attacks, but we also use it to signal the login type to the final auth page
    session.setAttribute("antiCSRF", facebookAntiCSRF);

    // get Facebook OAuth Login URL
    String loginUrl = null;
    try {
        loginUrl = isMobile ?
                FacebookAuth.getLoginUrlMobile(request.getScheme() + "://" + request.getServerName(), facebookAntiCSRF, display) :
                FacebookAuth.getLoginUrlWeb(request.getScheme() + "://" + request.getServerName(), facebookAntiCSRF);
    } catch (LeanException e) {
        response.sendRedirect(scheme.getErrorUrl(e));
        return;
    }
    response.sendRedirect(loginUrl);

%>
