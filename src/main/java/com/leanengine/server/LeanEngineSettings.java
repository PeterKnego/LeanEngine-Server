package com.leanengine.server;

import com.google.appengine.api.datastore.*;

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
            return settings;
        } catch (EntityNotFoundException e) {
            return new HashMap<String, Object>();
        }
    }

    public static void saveSettings(Map<String, Object> settings) {
        // there is only one instance of LeanEngineSettings so the same ID=1 is always used
        Entity leanEntity = new Entity("_settings", 1);
        for (String propName : settings.keySet()) {
            leanEntity.setProperty(propName, settings.get(propName));
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(leanEntity);
    }

    public static String getFacebookAppID() {
//        if (settings == null) load();
//        return (String) settings.get("facebookAppID");
        return "167040553377961";
    }

    public static String getFacebookAppSecret() {
//        if (settings == null) load();
//        return (String) settings.get("facebookAppSecret");
        return "39d80791024bf21ca584a9204d6733da";
    }

    /**
     * Retrieves application settings.
     *
     * @return Map of application settings.
     */
    public static Map<String, Object> getSettings() {
        if (settings == null) load();
        return settings;
    }


}
