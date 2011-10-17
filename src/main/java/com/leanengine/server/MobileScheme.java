package com.leanengine.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MobileScheme implements Scheme {

    private String scheme = "leanengine://";
    private String hostname;

    public MobileScheme(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String getUrl(String authToken) {
        return getUrl(authToken, null);
    }

    @Override
    public String getUrl( String authToken, String redirectUrl) {
        return scheme + hostname + "/?auth_token=" + authToken;
    }

    @Override
    public String getErrorUrl(int errorCode, String errorMsg) {
        return getErrorUrl(errorCode, errorMsg, null);
    }

    @Override
    public String getErrorUrl( int errorCode, String errorMsg, String redirectUrl) {
        try {
            return scheme + hostname + "/?errorcode=" + errorCode + "&errormsg=" + URLEncoder.encode(errorMsg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not happen - UTF-8 is supported on all JVMs
            return null;
        }
    }
}
