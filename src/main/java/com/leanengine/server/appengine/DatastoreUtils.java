package com.leanengine.server.appengine;

import com.google.appengine.api.datastore.*;
import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import com.leanengine.server.auth.AuthService;
import com.leanengine.server.auth.AuthToken;
import com.leanengine.server.auth.LeanAccount;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QuerySort;

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

    public static Entity getPrivateEntity(String kind, String entityId) throws LeanException {
        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) return null;

        if (entityId == null || kind == null) return null;
        Entity entity;
        try {
            entity = datastore.get(KeyFactory.createKey(kind, entityId));
        } catch (EntityNotFoundException e) {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }
        return entity;
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

    public static List<Entity> getPrivateEntities(String kind) throws LeanException {
        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) throw new LeanException(LeanException.Error.NotAuthorized);

        Query query = new Query("lean_entity");
        if (kind != null) {
            query.addFilter("_kind", Query.FilterOperator.EQUAL, kind);
        }
        query.addFilter("_account", Query.FilterOperator.EQUAL, account.id);
        PreparedQuery pq = datastore.prepare(query);

        return pq.asList(FetchOptions.Builder.withDefaults());
    }

    public static long putPrivateEntity(String entityName, Map<String, Object> properties) throws LeanException {

        if (!pattern.matcher(entityName).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityName);
        }

        Entity entityEntity = new Entity("lean_entity");
        entityEntity.setProperty("_account", AuthService.getCurrentAccount().id);
        entityEntity.setProperty("_kind", entityName);

        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                entityEntity.setProperty(entry.getKey(), entry.getValue());
            }
        }
        Key result = datastore.put(entityEntity);
        return result.getId();
    }

    public static List<Entity> queryEntityPrivate(LeanQuery leanQuery) throws LeanException {
        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) throw new LeanException(LeanException.Error.NotAuthorized);

        Query query = new Query("lean_entity");
        query.addFilter("_kind", Query.FilterOperator.EQUAL, leanQuery.getKind());
        query.addFilter("_account", Query.FilterOperator.EQUAL, account.id);

        for (QueryFilter queryFilter : leanQuery.getFilters()) {
            query.addFilter(
                    queryFilter.getProperty(),
                    queryFilter.getOperator().getFilterOperator(),
                    queryFilter.getValue());
        }

        for (QuerySort querySort : leanQuery.getSorts()) {
            query.addSort(querySort.getProperty(), querySort.getDirection().getSortDirection());
        }

        PreparedQuery pq = datastore.prepare(query);

        return pq.asList(FetchOptions.Builder.withDefaults());
    }

}
