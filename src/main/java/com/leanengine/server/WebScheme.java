package com.leanengine.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebScheme implements Scheme {

    private String hostname;
    private String scheme;

    public WebScheme(String scheme, String hostname) {
        this.hostname = hostname;
        this.scheme = scheme + "://";
    }

    @Override
    public String getUrl(String authToken) {
        return getUrl(authToken, null);
    }

     @Override
    public String getUrl(String authToken, String redirectUrl) {
        // if null set default value
        redirectUrl = redirectUrl == null ? "/login/logindone.jsp" : redirectUrl;
        return scheme + hostname + redirectUrl + "?auth_token=" + authToken;
    }

    @Override
    public String getErrorUrl(int errorCode, String errorMsg) {
        return getErrorUrl(errorCode, errorMsg, null);
    }

    @Override
    public String getErrorUrl(int errorCode, String errorMsg, String redirectUrl) {
        // if null set default value
        redirectUrl = redirectUrl == null ? "/login/loginerror.jsp" : redirectUrl;
        try {
            return scheme + hostname + redirectUrl + "?errorlogin=true&errorcode" + errorCode + "&errormsg=" + URLEncoder.encode(errorMsg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not happen - UTF-8 is supported on all JVMs
            return null;
        }
    }

}
