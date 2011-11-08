package com.leanengine.server.auth;

import com.leanengine.server.LeanException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class MobileScheme implements Scheme {

    private static final Logger log = Logger.getLogger(MobileScheme.class.getName());

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
    public String getUrl(String authToken, String redirectUrl) {
        return scheme + hostname + "/?auth_token=" + authToken;
    }

    @Override
    public String getErrorUrl(LeanException exception) {
        return getErrorUrl(exception, null);
    }

    @Override
    public String getErrorUrl(LeanException exception, String redirectUrl) {

        log.severe(exception.getMessage());

        try {
            return scheme + hostname + "/?errorcode=" + exception.getErrorCode() +
                    "&errormsg=" + URLEncoder.encode(exception.getMessage(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not happen - UTF-8 is supported on all JVMs
            return null;
        }
    }
}
