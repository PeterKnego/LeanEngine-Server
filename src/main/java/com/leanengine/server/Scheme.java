package com.leanengine.server;

public interface Scheme {

    String getUrl(String authToken);

    String getUrl(String authToken, String redirectUrl);

    String getErrorUrl(LeanException exception);

    String getErrorUrl(LeanException exception, String redirectUrl);
}
