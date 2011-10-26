package com.leanengine.server.rest.resteasy;

import com.leanengine.server.LeanException;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestExceptionWrapper {

    public int code;
    public String message;
    public String cause;

    public RestExceptionWrapper(LeanException leanException) {
        code = leanException.getErrorCode();
        message = leanException.getMessage();
        cause = leanException.getCause() != null ? leanException.getCause().getMessage() : null;
    }
}
