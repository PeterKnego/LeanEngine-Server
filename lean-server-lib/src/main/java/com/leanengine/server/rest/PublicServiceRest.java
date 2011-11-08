package com.leanengine.server.rest;

import com.google.appengine.api.users.UserServiceFactory;
import com.leanengine.server.auth.AuthService;
import com.leanengine.server.auth.LeanAccount;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v1/public")
public class PublicServiceRest {

    private static PublicServiceRest instance;

    public static PublicServiceRest getInstance() {
        if (instance == null)
            instance = new PublicServiceRest();
        return instance;
    }


    @POST
    @Path("/loginurl")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String createLoginURL(String federatedIdentity) {
        return UserServiceFactory.getUserService().createLoginURL("/mobilereturn.jsp", null, federatedIdentity, null);
    }


    @GET
    @Path("/account")
    @Produces("application/json")
    public LeanAccount getCurrentAccount() {
        return AuthService.getCurrentAccount();
    }
}
