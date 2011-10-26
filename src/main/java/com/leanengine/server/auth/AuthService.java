package com.leanengine.server.auth;

import com.leanengine.server.appengine.DatastoreUtils;

import java.util.logging.Logger;

public class AuthService {

    private static final Logger log = Logger.getLogger(AuthService.class.getName());

    private static ThreadLocal<String> tlAuthToken = new ThreadLocal<String>();
    private static ThreadLocal<LeanAccount> tlLeanAccount = new ThreadLocal<LeanAccount>();

    public static void startAuthSession(String token) {
        LeanAccount user = getAccountByToken(token);
        tlAuthToken.set(token);
        tlLeanAccount.set(user);
    }

    public static void finishAuthSession() {
        tlLeanAccount.remove();
        tlAuthToken.remove();
    }


    private static LeanAccount getAccountByToken(String authToken) {

        //todo Use MemCache to cache this
        AuthToken savedToken = DatastoreUtils.getAuthToken(authToken);
        if (savedToken == null) return null;
        LeanAccount user = DatastoreUtils.getAccount(savedToken.accountID);
        if (user == null) return null;

        return user;
    }

    public static void resetCurrentAuthData() {
        String token = tlAuthToken.get();
        if (token != null) DatastoreUtils.removeAuthToken(token);
        tlLeanAccount.remove();
        tlAuthToken.remove();
    }

    public static AuthToken createAuthToken(long accountID) {
        AuthToken authToken = new AuthToken(accountID);
        DatastoreUtils.saveAuthToken(authToken);
        return authToken;
    }

    public static LeanAccount getCurrentAccount() {
        return tlLeanAccount.get();
    }

    public static boolean isUserLoggedIn() {
        return tlAuthToken.get() != null;
    }
}
