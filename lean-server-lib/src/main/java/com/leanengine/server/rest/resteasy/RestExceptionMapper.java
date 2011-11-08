package com.leanengine.server.rest.resteasy;

import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.leanengine.server.LeanException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger log = Logger.getLogger(RestExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        log.severe(exception.getMessage());

        if (exception instanceof LeanException) {
            LeanException leanException = (LeanException) exception;
            if (leanException.getErrorCode() >= 100) {
                // client error
                return Response.status(400).entity(new RestExceptionWrapper(leanException)).build();
            } else {
                // server error
                return Response.status(500).entity(new RestExceptionWrapper(leanException)).build();
            }
        } else {
            return Response.status(500).entity(new RestExceptionWrapper(exception)).build();
        }
    }


}
