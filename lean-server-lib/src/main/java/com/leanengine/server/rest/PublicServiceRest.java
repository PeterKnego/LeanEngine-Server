package com.leanengine.server.rest;

import com.leanengine.server.LeanException;
import com.leanengine.server.auth.AuthService;
import com.leanengine.server.auth.LeanAccount;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/v1/public")
public class PublicServiceRest {

    private static PublicServiceRest instance;

    public static PublicServiceRest getInstance() {
        if (instance == null)
            instance = new PublicServiceRest();
        return instance;
    }

    @GET
    @Path("/account")
    @Produces("application/json")
    public String getCurrentAccount() throws LeanException {
        LeanAccount account = AuthService.getCurrentAccount();
        return account!=null ? account.toJson() : "{}";
    }

    @GET
    @Path("/logout")
    @Produces("application/json")
    public String logout() {
        // returns 'false' of user is not logged in
        if (AuthService.getCurrentAccount() == null) {
            return "{\"result\":false}";
        } else {
            AuthService.resetCurrentAuthData();
            return "{\"result\":true}";
        }
    }
}
