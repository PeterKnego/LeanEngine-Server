package com.leanengine.server.auth;

import com.leanengine.server.LeanException;

public interface Scheme {

//    String getUrl(String authToken);

    String getUrl(String authToken, String redirectUrl);

    String getErrorUrl(LeanException exception);

    String getErrorUrl(LeanException exception, String redirectUrl);
}
