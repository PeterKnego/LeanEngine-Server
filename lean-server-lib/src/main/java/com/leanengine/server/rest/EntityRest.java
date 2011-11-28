package com.leanengine.server.rest;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import org.codehaus.jackson.JsonNode;

import javax.ws.rs.*;
import java.util.List;
import java.util.logging.Logger;

@Path("/v1/entity")
@Produces("application/json")
@Consumes("application/json")
public class EntityRest {

    private static final Logger log = Logger.getLogger(EntityRest.class.getName());

    @GET
    @Path("/{entityName}/{entityId}")
    public JsonNode getEntity(@PathParam("entityName") String entityName, @PathParam("entityId") long entityId) throws LeanException {
        Entity entity = DatastoreUtils.getPrivateEntity(entityName, entityId);
        return JsonUtils.entityToJson(entity);
    }

    @DELETE
    @Path("/{entityName}/{entityId}")
    public void deleteEntity(@PathParam("entityName") String entityName, @PathParam("entityId") long entityId) throws LeanException {
         DatastoreUtils.deletePrivateEntity(entityName, entityId);
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
        List<Entity> entities = DatastoreUtils.getPrivateEntities();
        return JsonUtils.entityListToJson(entities);
    }

    @POST
    @Path("/{entityName}")
    public String putEntity(@PathParam("entityName") String entityName, JsonNode entityJson) throws LeanException {
        long entityID = DatastoreUtils.putPrivateEntity(entityName, JsonUtils.entityPropertiesFromJson(entityJson));
        return "{\"id\":" + entityID + "}";
    }

}
