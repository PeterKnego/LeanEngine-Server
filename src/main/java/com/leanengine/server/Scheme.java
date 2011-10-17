package com.leanengine.server;

public interface Scheme {

    String getUrl(String authToken);

    String getUrl(String authToken, String redirectUrl);

    String getErrorUrl(int errorCode, String errorMsg);

    String getErrorUrl(int errorCode, String errorMsg, String redirectUrl);
}
