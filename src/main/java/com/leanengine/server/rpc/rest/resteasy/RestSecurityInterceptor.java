package com.leanengine.server.rpc.rest.resteasy;

import com.leanengine.server.AuthService;
import com.leanengine.server.rpc.rest.PublicServiceRest;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.UnauthorizedException;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
@ServerInterceptor
public class RestSecurityInterceptor implements PreProcessInterceptor {

    private static final Logger log = Logger.getLogger(RestSecurityInterceptor.class.getName());

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws UnauthorizedException {

        // pass public methods
        if (method.getResourceClass().equals(PublicServiceRest.class)) {
            return null;
        }

        // user not logged-in?
        if (AuthService.getCurrentAccount() == null) {
            ServerResponse response = new ServerResponse();
            response.setStatus(HttpResponseCodes.SC_UNAUTHORIZED);
            MultivaluedMap<String, Object> headers = new Headers<Object>();
            headers.add("Content-Type", "text/plain");
            response.setMetadata(headers);
            response.setEntity("Error 401 Unauthorized: " + request.getPreprocessedPath());
            return response;
        }
        return null;
    }
}
