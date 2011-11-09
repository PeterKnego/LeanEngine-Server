package com.leanengine.server.auth;

import com.leanengine.server.LeanException;

public interface Scheme {

    String getUrl(String authToken, String redirectUrl) throws LeanException;

    String getErrorUrl(LeanException exception, String redirectUrl);
}
