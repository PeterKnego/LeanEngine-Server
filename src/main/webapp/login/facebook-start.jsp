<%@ page import="com.leanengine.server.*" %>
<%@ page import="java.util.UUID" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String type = request.getParameter("type");
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
        response.sendRedirect(scheme.getErrorUrl(2, "Server not configured properly: Facebook Login is not enabled."));
    }

    if (LeanEngineSettings.getFacebookAppID() == null) {
        // error: facebookAppID not set
        response.sendRedirect(scheme.getErrorUrl(2, "Server not configured properly: missing Facebook Application ID"));
    }

    // 'state' parameter is passed around the Facebook OAuth redirects and ends up at our final auth page
    // Primarily it's used for preventing CSRF attacks, but we also use it to signal the login type to the final auth page
    session.setAttribute("antiCSRF", facebookAntiCSRF);

    // get Facebook OAuth Login URL
    String loginUrl = isMobile ?
            FacebookAuth.getLoginUrlMobile(request.getScheme() + "://" + request.getServerName(), facebookAntiCSRF) :
            FacebookAuth.getLoginUrlWeb(request.getScheme() + "://" + request.getServerName(), facebookAntiCSRF);
    response.sendRedirect(loginUrl);

%>
