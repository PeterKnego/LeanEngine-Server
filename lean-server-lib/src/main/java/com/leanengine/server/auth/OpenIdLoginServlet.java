package com.leanengine.server.auth;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.leanengine.server.LeanEngineSettings;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.AccountUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenIdLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String nextUrl = request.getParameter("next");

        if (nextUrl == null) { // first part of the OpenID flow

            String type = request.getParameter("type") == null ? "web" : request.getParameter("type");

            String errorUrl;
            if (request.getParameter("onerror") != null) {
                errorUrl = request.getParameter("onerror");
            } else {
                errorUrl = "/loginerror";
            }

            // redirectUrl is composed so that it redirects twice:
            // first to /login/openid-auth.jsp for authentication
            // second to the final destination URL
            String redirectUrl;
            if (type.equals("mobile")) {
                redirectUrl = "/openid?next=@mobile";
            } else {
                String redirectParam;
                if (request.getParameter("redirect") != null) {
                    redirectParam = request.getParameter("redirect");
                } else {
                    redirectParam = "/";
                }
                redirectUrl = "/openid?next=" + redirectParam + "@" + errorUrl;
            }

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
                response.sendRedirect(
                        scheme.getErrorUrl(new LeanException(LeanException.Error.OpenIdAuthNotEnabled), "/loginerror"));
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

            String loginUrl = UserServiceFactory.getUserService().createLoginURL(redirectUrl, null, openIdProvider, null);

            response.sendRedirect(loginUrl);


        } else { // second part of the OpenID flow
            // type parameters tells us the type of redirect we should perform
            Scheme scheme;
            String redirectUrl = null;
            String errorUrl = null;

            if (nextUrl.equals("@mobile")) {
                scheme = new MobileScheme(request.getServerName());
            } else {
                String hostname = request.getServerName();
                if (request.getLocalPort() != 80 && request.getLocalPort() != 0) {
                    hostname = hostname + ":" + request.getLocalPort();
                }
                scheme = new WebScheme(request.getScheme(), hostname);

                // extract the redirect URL
                String[] stateItems = nextUrl.split("@");
                redirectUrl = (stateItems.length == 2) ? stateItems[0] : null;
                errorUrl = (stateItems.length == 2) ? stateItems[1] : null;
            }


            // get user
            User currentUser = UserServiceFactory.getUserService().getCurrentUser();

            //OpenID login did not succeed
            if (currentUser == null) {
                response.sendRedirect(
                        scheme.getErrorUrl(new LeanException(LeanException.Error.OpenIdAuthFailed), errorUrl));
                return;
            }
            // get toke for this user
            AuthToken authToken;

            LeanAccount account = AccountUtils.findAccountByProvider(currentUser.getUserId(),
                    currentUser.getFederatedIdentity());

            if (account == null) {
                //todo this is one-to-one mapping between Account and User - change this in the future

                Map<String, Object> props = new HashMap<String, Object>();
                props.put("email", currentUser.getEmail());

                // account does not yet exist - create it
                account = new LeanAccount(
                        0,
                        currentUser.getNickname(),
                        currentUser.getUserId(),
                        currentUser.getFederatedIdentity(),
                        props
                );

                // saving the LeanAccount sets the 'id' on it
                AccountUtils.saveAccount(account);
            }

            // create our own authentication token
            authToken = AuthService.createAuthToken(account.id);

            // save token in session
            HttpSession session = request.getSession(true);
            session.setAttribute("lean_token", authToken.token);

            //send lean_token back to browser
            try {
                response.sendRedirect(scheme.getUrl(authToken.token, redirectUrl));
            } catch (LeanException e) {
                response.sendRedirect(scheme.getErrorUrl(e, errorUrl));
            }
        }
    }
}
