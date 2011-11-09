package com.leanengine.server;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.utils.SystemProperty;
import com.leanengine.server.appengine.ServerUtils;

import java.util.HashMap;
import java.util.Map;

public class LeanEngineSettings {

    private static Map<String, Object> settings;

    private static Map<String, Object> load() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key key = KeyFactory.createKey("_settings", 1);
        Entity leanEntity;
        try {
            leanEntity = datastore.get(key);
            settings = leanEntity.getProperties();
        } catch (EntityNotFoundException e) {
            settings = new HashMap<String, Object>();

            // By default enable all logins on Development server
            if (ServerUtils.isDevServer()) {
                settings.put("fbLoginEnable", true);
                settings.put("fbAppId", "mockFacebookAppId");
                settings.put("fbAppSecret", "mockFacebookAppSecret");
                settings.put("openIdLoginEnable", true);
                saveSettings(settings);
            }
        }
        return settings;
    }

    public static void saveSettings(Map<String, Object> newSettings) {
        // there is only one instance of LeanEngineSettings so the same ID=1 is always used
        Entity leanEntity = new Entity("_settings", 1);
        for (String propName : newSettings.keySet()) {
            leanEntity.setProperty(propName, newSettings.get(propName));
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(leanEntity);
        LeanEngineSettings.settings = newSettings;
    }

    public static boolean isFacebookLoginEnabled() {
        if (settings == null) load();
        Object fbLoginEnable = settings.get("fbLoginEnable");
        return fbLoginEnable == null ? false : (Boolean) fbLoginEnable;
    }


    public static String getFacebookAppID() {
        if (settings == null) load();
        return (String) settings.get("fbAppId");
    }

    public static String getFacebookAppSecret() {
        if (settings == null) load();
        return (String) settings.get("fbAppSecret");
    }

    public static boolean isOpenIdLoginEnabled() {
        if (settings == null) load();
        Object openIdLoginEnable = settings.get("openIdLoginEnable");
        return openIdLoginEnable == null ? false : (Boolean) openIdLoginEnable;
    }

    /**
     * Retrieves application settings.
     *
     * @return Map of application settings.
     */
    public static Map<String, Object> getSettings() {
        load();
        return settings;
    }

    /**
     * Helper class for one-line saving of multiple settings.
     */
    public static class Builder {

        private Map<String, Object> temp = new HashMap<String, Object>();

        public Builder add(String name, Object value) {
            temp.put(name, value);
            return this;
        }

        public void save() {
            if (!temp.isEmpty()) LeanEngineSettings.saveSettings(temp);
        }

    }

}
