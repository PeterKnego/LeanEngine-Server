package com.leanengine.server.auth;

import com.leanengine.server.LeanException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class WebScheme implements Scheme {

    private static final Logger log = Logger.getLogger(WebScheme.class.getName());

    private String hostname;
    private String scheme;

    public WebScheme(String scheme, String hostname) {
        this.hostname = hostname;
        this.scheme = scheme + "://";
    }

     @Override
    public String getUrl(String authToken, String redirectUrl) throws LeanException {
        // if null throw error
        if(redirectUrl ==null)
            throw new LeanException(LeanException.Error.MissingRedirectUrl);
        return scheme + hostname + redirectUrl;
    }

    @Override
    public String getErrorUrl(LeanException exception, String redirectUrl) {

        log.severe(exception.getMessage());

        // if null set default value
        redirectUrl = redirectUrl == null ? "/loginerror" : redirectUrl;
        try {
            return scheme + hostname + redirectUrl + "?errorlogin=true&errorcode=" + exception.getErrorCode() +
                    "&errormsg=" + URLEncoder.encode(exception.getMessage(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not happen - UTF-8 is supported on all JVMs
            return null;
        }
    }

}
