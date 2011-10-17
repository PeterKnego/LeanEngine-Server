package com.leanengine.server.rpc.rest.resteasy;

import com.leanengine.server.LeanException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<LeanException> {

    @Override
    public Response toResponse(LeanException exception) {
        if (exception.getErrorCode() >= 100) {
            // client error
            return Response.status(400).entity(new RestExceptionWrapper(exception)).build();
        } else {
            // server error
            return Response.status(500).entity(new RestExceptionWrapper(exception)).build();
        }
    }
}
