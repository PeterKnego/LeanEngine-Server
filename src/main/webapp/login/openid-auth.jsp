<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.leanengine.server.*" %>
<%@ page import="com.leanengine.server.appengine.DatastoreUtils" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String type = request.getParameter("type") == null ? "web" : request.getParameter("type");

    // type parameters tells us the type of redirect we should perform
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

    String nextUrl = request.getParameter("next");

    // get user
    User currentUser = UserServiceFactory.getUserService().getCurrentUser();

    //OpenID login did not succeed
    if (currentUser == null) {
        response.sendRedirect(scheme.getErrorUrl(15, "OpenID authentication failed."));
    }
    // get toke for this user
    AuthToken authToken;

    LeanAccount account = DatastoreUtils.findAccountByProvider(currentUser.getUserId(),
            currentUser.getFederatedIdentity());

    if (account == null) {
        //todo this is one-to-one mapping between Account and User
        //change this in the future

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
        DatastoreUtils.saveAccount(account);
    }

    // create our own authentication token
    authToken = AuthService.createAuthToken(account.id);

    // save token in session
    session.setAttribute("lean_token", authToken.token);

    //send lean_token back to browser
    response.sendRedirect(scheme.getUrl(authToken.token, nextUrl));       // todo null
%>