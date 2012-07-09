package com.leanengine.server.auth;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanEngineSettings;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.AccountUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class FacebookAuth {

    private static final Logger log = Logger.getLogger(FacebookAuth.class.getName());

    public static String getLoginUrlMobile(String serverName, String state, String facebookLoginPath, String display) throws LeanException {
        if (LeanEngineSettings.getFacebookAppID() == null) {
            throw new LeanException(LeanException.Error.FacebookAuthMissingAppId);
        }

        String redirectUrl = serverName + facebookLoginPath;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("https://m.facebook.com/dialog/oauth?")
                .append("client_id=").append(LeanEngineSettings.getFacebookAppID()).append("&")
                .append("redirect_uri=").append(redirectUrl).append("&");

        if (display != null)
            stringBuilder.append("display=").append(display).append("&");

        stringBuilder.append("scope=offline_access&")
                .append("response_type=code&")
                .append("state=")
                .append(state);
        return stringBuilder.toString();
    }

    public static String getLoginUrlWeb(String serverName, String state, String facebookLoginPath) throws LeanException {
        if (LeanEngineSettings.getFacebookAppID() == null) {
            throw new LeanException(LeanException.Error.FacebookAuthMissingAppId);
        }

        String redirectUrl = serverName + facebookLoginPath;
        return "https://www.facebook.com/dialog/oauth?" +
                "client_id=" + LeanEngineSettings.getFacebookAppID() + "&" +
                "redirect_uri=" + redirectUrl + "&" +
                "scope=offline_access&" +
                "response_type=code&" +
                "state=" + state;
    }

    public static String getGraphAuthUrl(String redirectUrl, String code) throws LeanException {
        if (LeanEngineSettings.getFacebookAppID() == null) {
            throw new LeanException(LeanException.Error.FacebookAuthMissingAppId);
        }

        if (LeanEngineSettings.getFacebookAppID() == null) {
            throw new LeanException(LeanException.Error.FacebookAuthMissingAppSecret);
        }

        return "https://graph.facebook.com/oauth/access_token?" +
                "client_id=" + LeanEngineSettings.getFacebookAppID() + "&" +
                "redirect_uri=" + redirectUrl + "&" +
                "client_secret=" + LeanEngineSettings.getFacebookAppSecret() + "&" +
                "code=" + code;
    }

    public static JsonNode fetchUserDataFromFacebook(String access_token) throws LeanException {
        String url = "https://graph.facebook.com/me?access_token=" + access_token;
        URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
        HTTPResponse fetchResponse;
        try {
            fetchResponse = fetchService.fetch(new URL(url));
            if (fetchResponse.getResponseCode() == 200) {
                return JsonUtils.getObjectMapper().readTree(new String(fetchResponse.getContent(), "UTF-8"));
            } else {
                throw new LeanException(LeanException.Error.FacebookAuthError);
            }
        } catch (MalformedURLException ex) {
            //todo This should not happen - log it
        } catch (JsonProcessingException ex) {
            throw new LeanException(LeanException.Error.FacebookAuthParseError, ex);
        } catch (IOException e) {
            throw new LeanException(LeanException.Error.FacebookAuthConnectError, e);
        }
        return null;
    }

    public static AuthToken authenticateWithOAuthGraphAPI(String currentUrl, String code) throws LeanException {

        try {
            URL facebookGraphUrl = new URL(FacebookAuth.getGraphAuthUrl(currentUrl, code));
            log.info("facebookGraphUrl="+facebookGraphUrl);
            URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
            HTTPResponse fetchResponse = fetchService.fetch(facebookGraphUrl);
            String responseContent = new String(fetchResponse.getContent(), Charset.forName("UTF-8"));
            if (fetchResponse.getResponseCode() == 400) {

                // error: facebook server error replied with 400
                throw new LeanException(LeanException.Error.FacebookAuthResponseError," \n\n"+responseContent);
            }

            String fbAccessToken = null, expires = null;
            String[] splitResponse = responseContent.split("&");

            for (String split : splitResponse) {
                String[] parts = split.split("=");
                if (parts.length != 2) break;
                fbAccessToken = parts[0].equals("access_token") ? parts[1] : fbAccessToken;
                expires = parts[0].equals("expires") ? parts[1] : expires;
            }

            // check if we got required parameters
            if (fbAccessToken == null) {
                //error: wrong parameters: facebook should return 'access_token' parameter
                throw new LeanException(LeanException.Error.FacebookAuthMissingParam, " 'access_token' not available.");
            }

            // All is good - check the user
            JsonNode userData = FacebookAuth.fetchUserDataFromFacebook(fbAccessToken);
            String providerID = userData.get("id").getTextValue();

            if (providerID == null || providerID.length() == 0) {
                //Facebook returned user data but ID field is missing
                throw new LeanException(LeanException.Error.FacebookAuthMissingParam,
                        " Missing ID field in user data. Content: " + responseContent);
            }

            LeanAccount account = AccountUtils.findAccountByProvider(providerID, "fb-oauth");
            if (account == null) {
                //todo this is one-to-one mapping between Account and User
                //change this in the future

                // account does not yet exist - create it
                account = parseAccountFB(userData);
                AccountUtils.saveAccount(account);
            }

            // create our own authentication token
            return AuthService.createAuthToken(account.id);

        } catch (MalformedURLException e) {
            throw new LeanException(LeanException.Error.FacebookAuthNoConnection, e);
        } catch (IOException e) {
            throw new LeanException(LeanException.Error.FacebookAuthNoConnection, e);
        }
    }


    /**
     * Creates LeanAccount form data returned by Facebook authentication service
     *
     * @param userData JSON data as provided by Facebook OAuth
     * @return
     */
    public static LeanAccount parseAccountFB(JsonNode userData) {

        Map<String, Object> props = new HashMap<String, Object>(userData.size());
        Iterator<String> fields = userData.getFieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            // field 'id' is not treated as part of provider properties
            if (field.equals("id")) continue;
            props.put(field, userData.get(field).getValueAsText());
        }

        return new LeanAccount(
                0,
                userData.get("name").getTextValue(),
                userData.get("id").getTextValue(),
                "fb-oauth",
                props);
    }
}
