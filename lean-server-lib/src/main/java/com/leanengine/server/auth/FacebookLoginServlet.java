package com.leanengine.server.auth;

import com.leanengine.server.LeanEngineSettings;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.ServerUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

public class FacebookLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String errorCode = request.getParameter("error");
        String authorizationCode = request.getParameter("code");

        // checking the stage of a Facebook OAuth flow
        if (errorCode == null && authorizationCode == null) {  // first part of Facebook OAuth flow

            String type = request.getParameter("type");
            String display = request.getParameter("display");
            Scheme scheme;
            String facebookAntiCSRF;

            // checking if request is coming from mobile client
            boolean isMobile = type != null && type.equals("mobile");

            String redirectUrl;
            if (request.getParameter("onlogin") != null) {
                redirectUrl = request.getParameter("onlogin");
            } else {
                redirectUrl = "/";
            }

            String errorUrl;
            if (request.getParameter("onerror") != null) {
                errorUrl = request.getParameter("onerror");
            } else {
                errorUrl = "/loginerror";
            }

            if (isMobile) {
                // login via mobile device - this means redirecting to 'leanengine://hostname/?access_token=..' URLs
                scheme = new MobileScheme(request.getServerName());
                // we add 'mob:' in front of state parameter to indicate that request comes from mobile device
                facebookAntiCSRF = "mob:" + UUID.randomUUID().toString();
            } else {
                // login via web interface
                String hostname = request.getServerName();
                if (request.getLocalPort() != 80 && request.getLocalPort() != 0) {
                    hostname = hostname + ":" + request.getLocalPort();
                }
                scheme = new WebScheme(request.getScheme(), hostname);
                facebookAntiCSRF = "web:" + UUID.randomUUID().toString() + ":" + redirectUrl + ":" + errorUrl;
            }

            // Facebook login is not enabled in settings
            if (!LeanEngineSettings.isFacebookLoginEnabled()) {
                response.sendRedirect(
                        scheme.getErrorUrl(new LeanException(LeanException.Error.FacebookAuthNotEnabled), errorUrl));
                return;
            }

            // development server mocks Facebook logins
            if (ServerUtils.isDevServer()) {
                String mockEmail = request.getParameter("email");
                String action = request.getParameter("action");
                if (mockEmail == null) {
                    FacebookMockLogin.showForm(request, response, isMobile);
                } else {
                    if ("Log Out".equals(action)) {
                        response.sendRedirect(scheme.getErrorUrl(
                                new LeanException(LeanException.Error.FacebookAuthError, " User cancelled login."), errorUrl));
                    } else {
                        FacebookMockLogin.login(request, response, mockEmail, scheme, redirectUrl, errorUrl);
                    }
                }
                return;
            }

            // 'state' parameter is passed around the Facebook OAuth redirects and ends up at our final auth page
            // Primarily it's used for preventing CSRF attacks, but we also use it to signal the login type to the final auth page
            HttpSession session = request.getSession(true);
            session.setAttribute("antiCSRF", facebookAntiCSRF);

            // get Facebook OAuth Login URL
            String loginUrl = null;
            try {
                loginUrl = isMobile ?
                        FacebookAuth.getLoginUrlMobile(request.getScheme() + "://" + request.getServerName(),
                                facebookAntiCSRF, request.getRequestURI(), display) :
                        FacebookAuth.getLoginUrlWeb(request.getScheme() + "://" + request.getServerName(),
                                facebookAntiCSRF, request.getRequestURI());
            } catch (LeanException e) {
                response.sendRedirect(scheme.getErrorUrl(e, errorUrl));
                return;
            }
            response.sendRedirect(loginUrl);

        } else {  // second part of Facebook OAuth flow

            // user is trying to login
            // reset the existing auth data if it exists
            AuthService.resetCurrentAuthData();
            HttpSession session = request.getSession(true);
            session.removeAttribute("lean_token");

            // include the port number - usually needed for Dev server
            String hostname = request.getServerName();
            if (request.getLocalPort() != 80 && request.getLocalPort() != 0) {
                hostname = hostname + ":" + request.getLocalPort();
            }

            // get the 'state' parameter containing CSRF code
            String state = request.getParameter("state");
            // 'state' must be equal to 'antiCSRF' attribute saved to web session
            if (state == null || !state.equals(session.getAttribute("antiCSRF"))) {
                // oauth error - redirect back to client with error
                response.sendRedirect(new WebScheme(request.getScheme(),
                        hostname).getErrorUrl(new LeanException(LeanException.Error.FacebookAuthMissingCRSF), "/"));
                return;
            }

            String redirectUrl = null;
            String errorUrl = null;

            // 'state' parameter has format "login_type:CSRFtoken:redirect_url:error_url"
            // extract the login type from 'state' parameter
            Scheme scheme;
            if (state.startsWith("mob:")) {
                scheme = new MobileScheme(request.getServerName());
            } else {
                scheme = new WebScheme(request.getScheme(), hostname);

                // extract the redirect URL
                String[] stateItems = state.split(":");
                redirectUrl = (stateItems.length == 4) ? stateItems[2] : null;
                errorUrl = (stateItems.length == 4) ? stateItems[3] : null;
            }

            // error url might not have been supplied if this second part of the flow was invoked directly
            errorUrl = (errorUrl == null || errorUrl.isEmpty()) ? "/loginerror" : errorUrl;

            // did Facebook OAuth return error?
            if (errorCode != null) {
                // oauth error - redirect back to client with error
                response.sendRedirect(scheme.getErrorUrl(
                        new LeanException(LeanException.Error.FacebookAuthError, " OAuth error: " + errorCode), errorUrl));

            } else { // no error
                String currentUrl = request.getRequestURL().toString();

                try {
                    // authenticate with Facebook Graph OAuth API
                    // this makes a direct connection from server to 'https://graph.facebook.com/oauth/access_token'
                    AuthToken lean_token = FacebookAuth.authenticateWithOAuthGraphAPI(currentUrl, authorizationCode);

                    // save token in session
                    session.setAttribute("lean_token", lean_token.token);

                    //send lean_token back to browser
                    response.sendRedirect(scheme.getUrl(lean_token.token, redirectUrl));

                } catch (LeanException le) {
                    response.sendRedirect(scheme.getErrorUrl(le, errorUrl));
                }
            }
        }
    }
}
