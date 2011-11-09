package com.leanengine.server.auth;

import com.leanengine.server.LeanEngineSettings;
import com.leanengine.server.LeanException;
import com.leanengine.server.auth.*;

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

        String error = request.getParameter("error");
        String code = request.getParameter("code");

        if (error == null && code == null) {  // first part of Facebook OAuth flow




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
                response.sendRedirect(scheme.getErrorUrl(e));
                return;
            }
            response.sendRedirect(loginUrl);


        } else {  // second part of Facebook OAuth flow

            // user is trying to login
            // reset the existing auth data if it exists
            AuthService.resetCurrentAuthData();
            HttpSession session = request.getSession(true);
            session.removeAttribute("lean_token");

            String state = request.getParameter("state");
            if (state == null || !state.equals(session.getAttribute("antiCSRF"))) {
                // oauth error - redirect back to client with error
                response.sendRedirect(new WebScheme(request.getScheme(), request.getServerName()).getErrorUrl(
                        new LeanException(LeanException.Error.FacebookAuthMissingCRSF)));
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

            if (error != null) {
                // oauth error - redirect back to client with error
                response.sendRedirect(scheme.getErrorUrl(
                        new LeanException(LeanException.Error.FacebookAuthError, "OAuth error: " + error)));

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
                    response.sendRedirect(scheme.getErrorUrl(le));
                }
            } else {
                // error: this should not happen - redirect back to client with error
                response.sendRedirect(scheme.getErrorUrl(new LeanException(LeanException.Error.FacebookAuthMissingParam)));

            }
        }
    }
}
