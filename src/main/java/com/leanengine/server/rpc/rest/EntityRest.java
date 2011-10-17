package com.leanengine.server.rpc.rest;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import org.codehaus.jackson.JsonNode;
import org.json.JSONException;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

@Path("/entity")
public class EntityRest {

    private static final Logger log = Logger.getLogger(EntityRest.class.getName());

    @GET
    @Path("/{entityName}/{entityId}")
    @Produces("application/json")
    public String getEntity(@PathParam("entityName") String entityName, @PathParam("entityId") String entityId) throws LeanException {
        return DatastoreUtils.getPrivateEntity(entityName, entityId);
    }

    @GET
    @Path("/")
    @Produces("application/json")
    public String getAllUserPrivateEntities() throws LeanException {
        return DatastoreUtils.getPrivateEntitiesAsJSON();
    }

    @POST
    @Path("/{entityName}")
    @Produces("application/json")
    @Consumes("application/json")
    public String putEntity(@PathParam("entityName") String entityName, JsonNode entityJson) throws LeanException {

        return DatastoreUtils.putPrivateEntity(entityName, getProperties(entityJson));
    }


    private Map<String, Object> getProperties(JsonNode jsonNode) throws LeanException {
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
