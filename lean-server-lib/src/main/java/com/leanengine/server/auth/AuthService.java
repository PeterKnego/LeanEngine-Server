package com.leanengine.server.auth;

import com.leanengine.server.appengine.AccountUtils;
import com.leanengine.server.appengine.ServerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    public static AuthToken createMockFacebookAccount(String email) {
        if(!ServerUtils.isDevServer()){
            throw new IllegalStateException("Method 'createMockFacebookAccount(email)' should only be called while running Dev Server.");
        }

        LeanAccount account = AccountUtils.findAccountByEmail(email, "fb-oauth");
        if (account == null) {
            //todo this is one-to-one mapping between Account and User
            //change this in the future

            Map<String, Object> props = new HashMap<String, Object>(1);
            props.put("email", email);

            // account does not yet exist - create it
            account = new LeanAccount(
                    0,
                    email,
                    UUID.randomUUID().toString(),
                    "fb-oauth",
                    props);
            AccountUtils.saveAccount(account);
        }

        // create our own authentication token
        // todo retrieve existing token if not expired
        return AuthService.createAuthToken(account.id);
    }

    private static LeanAccount getAccountByToken(String authToken) {

        //todo Use MemCache to cache this
        AuthToken savedToken = AccountUtils.getAuthToken(authToken);
        if (savedToken == null) return null;
        LeanAccount user = AccountUtils.getAccount(savedToken.accountID);
        if (user == null) return null;

        return user;
    }

    public static void resetCurrentAuthData() {
        String token = tlAuthToken.get();
        if (token != null) AccountUtils.removeAuthToken(token);
        tlLeanAccount.remove();
        tlAuthToken.remove();
    }

    public static AuthToken createAuthToken(long accountID) {
        AuthToken authToken = new AuthToken(accountID);
        AccountUtils.saveAuthToken(authToken);
        return authToken;
    }

    public static LeanAccount getCurrentAccount() {
        return tlLeanAccount.get();
    }

    public static boolean isUserLoggedIn() {
        return tlAuthToken.get() != null;
    }
}
