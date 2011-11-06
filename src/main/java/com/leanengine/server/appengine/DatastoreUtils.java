package com.leanengine.server.appengine;

import com.google.appengine.api.datastore.*;
import com.leanengine.server.LeanException;
import com.leanengine.server.auth.AuthService;
import com.leanengine.server.auth.LeanAccount;
import com.leanengine.server.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DatastoreUtils {

    private static final Logger log = Logger.getLogger(DatastoreUtils.class.getName());

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static Pattern pattern = Pattern.compile("^[A-Za-z][A-Za-z_0-9]*");

    public static Entity getPrivateEntity(String kind, String entityId) throws LeanException {
        findCurrentAccount();

        if (entityId == null || kind == null) throw new LeanException(LeanException.Error.EntityNotFound,
                " Entity 'kind' and 'id' must NOT be null.");

        long entityIdLong = Long.valueOf(entityId);
        Entity entity;
        try {
            entity = datastore.get(KeyFactory.createKey(kind, entityIdLong));
        } catch (EntityNotFoundException e) {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }
        return entity;
    }


    public static void deletePrivateEntity(String entityKind, String entityId) throws LeanException {
        LeanAccount account = findCurrentAccount();

        if (entityId == null || entityKind == null) throw new LeanException(LeanException.Error.EntityNotFound,
                " Entity 'kind' and 'id' must NOT be null.");

        long entityIdLong = Long.valueOf(entityId);
        Entity entity;
        try {
            entity = datastore.get(KeyFactory.createKey(entityKind, entityIdLong));
        } catch (EntityNotFoundException e) {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }

        if (account.id != (Long) entity.getProperty("_account"))
            throw new LeanException(LeanException.Error.NotAuthorized,
                    " Account not authorized to access entity '" + entityKind + "'with ID '" + entityId + "'");

        datastore.delete(entity.getKey());
    }

    private static LeanAccount findCurrentAccount() throws LeanException {
        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) throw new LeanException(LeanException.Error.NotAuthorized);
        return account;
    }

    public static List<Entity> getPrivateEntities() throws LeanException {
        findCurrentAccount();

        List<String> kindNames = findAllEtityKinds();

        List<Entity> result = new ArrayList<Entity>();

        for (String kindName : kindNames) {
            result.addAll(getPrivateEntities(kindName));
        }

        return result;
    }

    public static List<Entity> getPrivateEntities(String kind) throws LeanException {

        if (!pattern.matcher(kind).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityName);
        }

        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) throw new LeanException(LeanException.Error.NotAuthorized);

        Query query = new Query(kind);
        query.addFilter("_account", Query.FilterOperator.EQUAL, account.id);
        PreparedQuery pq = datastore.prepare(query);

        return pq.asList(FetchOptions.Builder.withDefaults());
    }

    public static long putPrivateEntity(String entityName, Map<String, Object> properties) throws LeanException {

        if (!pattern.matcher(entityName).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityName);
        }

        Entity entityEntity = new Entity(entityName);
        entityEntity.setProperty("_account", AuthService.getCurrentAccount().id);

        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                entityEntity.setProperty(entry.getKey(), entry.getValue());
            }
        }
        Key result = datastore.put(entityEntity);
        return result.getId();
    }

    public static QueryResult queryEntityPrivate(LeanQuery leanQuery) throws LeanException {
        LeanAccount account = findCurrentAccount();

        Query query = new Query(leanQuery.getKind());
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

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        QueryOptions options = leanQuery.getQueryOptions();
        if (leanQuery.getQueryOptions() != null) {
            if (options.getLimit() != null)
                fetchOptions.limit(options.getLimit());
            if (options.getOffset() != null)
                fetchOptions.offset(options.getOffset());
            if (options.getPrefetchSize() != null)
                fetchOptions.prefetchSize(options.getPrefetchSize());
            if (options.getStartCursor() != null)
                fetchOptions.startCursor(Cursor.fromWebSafeString(options.getStartCursor()));
            if (options.getEndCursor() != null)
                fetchOptions.endCursor(Cursor.fromWebSafeString(options.getEndCursor()));
        }

        PreparedQuery pq = datastore.prepare(query);

        QueryResultList<Entity> result;
        try {
            result = pq.asQueryResultList(fetchOptions);
        } catch (DatastoreNeedIndexException dnie) {
            throw new LeanException(LeanException.Error.AppEngineMissingIndex, dnie);
        }
        return new QueryResult(result, result.getCursor());
    }

    public static List<String> findAllEtityKinds() throws LeanException {

        Query q = new Query(Query.KIND_METADATA_KIND);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());

        List<String> result = new ArrayList<String>();
        for (Entity entity : list) {
            if (!entity.getKey().getName().startsWith("_"))
                result.add(entity.getKey().getName());
        }

        return result;

    }

}
