package com.leanengine.server.rest;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.LeanException;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QuerySort;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.TypeReference;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

@Path("/v1/entity")
@Produces("application/json")
@Consumes("application/json")
public class EntityRest {

    private static final Logger log = Logger.getLogger(EntityRest.class.getName());

    @GET
    @Path("/{entityName}/{entityId}")
    public String getEntity(@PathParam("entityName") String entityName, @PathParam("entityId") String entityId) throws LeanException {
        return DatastoreUtils.getPrivateEntity(entityName, entityId);
    }

    @GET
    @Path("/{entityName}")
    public String getAllUserPrivateEntities(@PathParam("entityName") String kind) throws LeanException {
        return DatastoreUtils.getPrivateEntitiesAsJSON(kind);
    }

    @GET
    @Path("/")
    public String getAllUserPrivateEntities() throws LeanException {
        return DatastoreUtils.getPrivateEntitiesAsJSON(null);
    }

    @POST
    @Path("/{entityName}")
    public String putEntity(@PathParam("entityName") String entityName, JsonNode entityJson) throws LeanException {
        return DatastoreUtils.putPrivateEntity(entityName, getEntityProperties(entityJson));
    }

    @POST
    @Path("/query")
    public ResultList<Entity> query(String queryJson) throws LeanException {
        return DatastoreUtils.queryEntityPrivate(LeanQuery.parseJSON(queryJson));
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
