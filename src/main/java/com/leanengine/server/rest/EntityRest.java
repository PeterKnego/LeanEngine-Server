package com.leanengine.server.rest;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QuerySort;
import org.codehaus.jackson.JsonNode;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/v1/entity")
@Produces("application/json")
@Consumes("application/json")
public class EntityRest {

    private static final Logger log = Logger.getLogger(EntityRest.class.getName());

    @GET
    @Path("/{entityName}/{entityId}")
    public JsonNode getEntity(@PathParam("entityName") String entityName, @PathParam("entityId") String entityId) throws LeanException {
        Entity entity = DatastoreUtils.getPrivateEntity(entityName, entityId);
        return JsonUtils.entityToJson(entity);
    }

    @GET
    @Path("/{entityName}")
    public JsonNode getAllUserPrivateEntities(@PathParam("entityName") String kind) throws LeanException {
        List<Entity> entities = DatastoreUtils.getPrivateEntities(kind);
        return JsonUtils.entityListToJson(entities);
    }

    @GET
    @Path("/")
    public JsonNode getAllUserPrivateEntities() throws LeanException {
        List<Entity> entities = DatastoreUtils.getPrivateEntities(null);
        return JsonUtils.entityListToJson(entities);
    }

    @POST
    @Path("/{entityName}")
    public String putEntity(@PathParam("entityName") String entityName, JsonNode entityJson) throws LeanException {
        long entityID = DatastoreUtils.putPrivateEntity(entityName, getEntityProperties(entityJson));
        return "{\"id\":" + entityID + "}";
    }

    @POST
    @Path("/query")
    public JsonNode query(String queryJson) throws LeanException {
        List<Entity> result = DatastoreUtils.queryEntityPrivate(LeanQuery.parseJSON(queryJson));
        return JsonUtils.entityListToJson(result);
    }

    @GET
    @Path("/query")
    public LeanQuery exampleQuery() throws LeanException {
        LeanQuery query = new LeanQuery("somekind");
        query.addFilter("prop1", QueryFilter.FilterOperator.EQUAL, "value1");
        query.addFilter("prop2", QueryFilter.FilterOperator.NOT_EQUAL, "not");
        query.addFilter("prop3", QueryFilter.FilterOperator.LESS_THAN_OR_EQUAL, 1.23);
        query.addFilter("prop4", QueryFilter.FilterOperator.IN, "inside");
        query.addSort("prop2", QuerySort.SortDirection.ASCENDING);
        return query;
    }

    private Map<String, Object> getEntityProperties(JsonNode jsonNode) throws LeanException {
        Map<String, Object> props = new HashMap<String, Object>(jsonNode.size());

        // must have some properties
        if (jsonNode.size() == 0) throw new LeanException(LeanException.Error.EmptyEntity);

        Iterator<String> fieldNames = jsonNode.getFieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();

            // skip LeanEngine system properties (starting with underscore '_')
            if (field.startsWith("_")) continue;
            JsonNode subNode = jsonNode.get(field);
            if (subNode.isValueNode()) {
                props.put(field, subNode.getValueAsText());
            } else {
                // todo Remove this when complex entity properties are introduced (GeoLoc, etc..)
                throw new LeanException(LeanException.Error.IllegalEntityFormat);
            }
        }
        return props;
    }
}
