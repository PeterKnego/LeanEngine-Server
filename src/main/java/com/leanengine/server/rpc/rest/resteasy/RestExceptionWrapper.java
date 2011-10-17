package com.leanengine.server.rpc.rest.resteasy;

import com.leanengine.server.LeanException;

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
