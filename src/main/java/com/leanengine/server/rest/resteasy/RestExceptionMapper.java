package com.leanengine.server.rest.resteasy;

import com.leanengine.server.LeanException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class RestExceptionMapper implements ExceptionMapper<LeanException> {

    private static final Logger log = Logger.getLogger(RestExceptionMapper.class.getName());

    @Override
    public Response toResponse(LeanException exception) {

        log.severe(exception.getMessage());

        if (exception.getErrorCode() >= 100) {
            // client error
            return Response.status(400).entity(new RestExceptionWrapper(exception)).build();
        } else {
            // server error
            return Response.status(500).entity(new RestExceptionWrapper(exception)).build();
        }
    }
}
