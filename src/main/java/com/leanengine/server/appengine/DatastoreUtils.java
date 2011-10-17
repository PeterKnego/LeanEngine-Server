package com.leanengine.server.appengine;

import com.google.appengine.api.datastore.*;
import com.leanengine.server.AuthService;
import com.leanengine.server.AuthToken;
import com.leanengine.server.LeanAccount;
import com.leanengine.server.LeanException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DatastoreUtils {

    private static final Logger log = Logger.getLogger(DatastoreUtils.class.getName());


    private static String authTokenKind = "_auth_tokens";
    private static String accountsKind = "_accounts";

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static Pattern pattern = Pattern.compile("^[A-Za-z][A-Za-z_0-9]*");

    public static LeanAccount getAccount(long accountID) {
        if (accountID <= 0) return null;
        Entity accountEntity;
        try {
            accountEntity = datastore.get(KeyFactory.createKey(accountsKind, accountID));
        } catch (EntityNotFoundException e) {
            return null;
        }

        return toLeanAccount(accountEntity);
    }

    public static LeanAccount findAccountByProvider(String providerID, String provider) {
        if (providerID == null) {
            log.severe("Empty providerID. Can not find account without providerID.");
            return null;
        }
        Query query = new Query(accountsKind);
        query.addFilter("_provider_id", Query.FilterOperator.EQUAL, providerID);
        query.addFilter("_provider", Query.FilterOperator.EQUAL, provider);
        PreparedQuery pq = datastore.prepare(query);

        Entity accountEntity = pq.asSingleEntity();

        return (accountEntity == null) ? null : toLeanAccount(accountEntity);
    }


    public static AuthToken getAuthToken(String token) {
        //todo use MemCache
        Entity tokenEntity;
        try {
            tokenEntity = datastore.get(KeyFactory.createKey(authTokenKind, token));
        } catch (EntityNotFoundException e) {
            return null;
        }

        return new AuthToken(
                token,
                (Long) tokenEntity.getProperty("account"),
                (Long) tokenEntity.getProperty("time")
        );
    }

    public static void saveAuthToken(AuthToken authToken) {
        //todo use MemCache

        Entity tokenEntity = new Entity(authTokenKind, authToken.token);

        tokenEntity.setProperty("account", authToken.accountID);
        tokenEntity.setProperty("time", authToken.timeCreated);
        datastore.put(tokenEntity);
    }

    public static void removeAuthToken(String token) {
        //todo use MemCache
        datastore.delete(KeyFactory.createKey(authTokenKind, token));
    }

    public static void saveAccount(LeanAccount leanAccount) {

        Entity accountEntity;

        // Is it a new LeanAccount? They do not have 'id' yet.
        if (leanAccount.id <= 0) {
            // create account
            accountEntity = new Entity(accountsKind);
        } else {
            // update account
            accountEntity = new Entity(accountsKind, leanAccount.id);
        }

        accountEntity.setProperty("_provider_id", leanAccount.providerId);
        accountEntity.setProperty("_provider", leanAccount.provider);
        accountEntity.setProperty("_nickname", leanAccount.nickName);
        for (Map.Entry<String, Object> property : leanAccount.providerProperties.entrySet()) {
            // properties must not start with underscore - this is reserved for system properties
            if (property.getKey().startsWith("_")) continue;
            accountEntity.setProperty(property.getKey(), property.getValue());
        }
        Key accountKey = datastore.put(accountEntity);
        leanAccount.id = accountKey.getId();
    }

    public static String getPrivateEntity(String kind, String entityId) throws LeanException {
        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) return null;

        if (entityId == null || kind == null) return null;
        Entity entity = null;
        try {
            entity = datastore.get(KeyFactory.createKey(kind, entityId));
        } catch (EntityNotFoundException e) {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }

        JSONObject json = entityToJson(entity);

        return json.toString();
    }

    private static JSONObject entityToJson(Entity entity) throws LeanException {
        JSONObject json = new JSONObject();
        try {
            json.put("_id", entity.getKey().getId());
            json.put("_entity", entity.getProperty("_entity"));
            json.put("_account", entity.getProperty("_account"));
            Map<String, Object> props = entity.getProperties();
            for (Map.Entry<String, Object> prop : props.entrySet()) {
                // todo handle proper typing of properties
                json.put(prop.getKey(), prop.getValue());
            }
        } catch (JSONException je) {
            throw new LeanException(LeanException.Error.EntityToJSON, je);
        }

        return json;
    }

    private static LeanAccount toLeanAccount(Entity entity) {
        LeanAccount account = new LeanAccount(
                entity.getKey().getId(),
                (String) entity.getProperty("_nickname"),
                (String) entity.getProperty("_provider_id"),
                (String) entity.getProperty("_provider"),
                entity.getProperties()
        );

        return account;
    }

    private static JSONObject toJson(List<Entity> entityList) throws LeanException {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();

        for (Entity entity : entityList) {
            array.put(entityToJson(entity));
        }

        try {
            json.put("list", array);
        } catch (JSONException e) {
            throw new LeanException(LeanException.Error.EntityToJSON, e);
        }
        return json;
    }

    public static String getPrivateEntitiesAsJSON() throws LeanException {
        return toJson(getPrivateEntities()).toString();
    }

    public static List<Entity> getPrivateEntities() {
        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) return null;

        Query query = new Query("lean_entity");
        query.addFilter("_account", Query.FilterOperator.EQUAL, account.id);
        PreparedQuery pq = datastore.prepare(query);

        return pq.asList(FetchOptions.Builder.withDefaults());
    }

    public static String putPrivateEntity(String entityName, Map<String, Object> properties) throws LeanException {

        if (!pattern.matcher(entityName).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityName);
        }

        Entity entityEntity = new Entity("lean_entity");
        entityEntity.setProperty("_account", AuthService.getCurrentAccount().id);
        entityEntity.setProperty("_entity", entityName);

        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                entityEntity.setProperty(entry.getKey(), entry.getValue());
            }
        }
        Key result = datastore.put(entityEntity);

        return "{\"id\":" + result.getId() + "}";
    }

}
