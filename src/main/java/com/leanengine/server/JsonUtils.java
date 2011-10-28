package com.leanengine.server;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.LeanException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static ThreadLocal<ObjectMapper> tlObjectMapper = new ThreadLocal<ObjectMapper>();

    /**
     * Returns a thread-local instance of JSON ObjectMapper.
     */
    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = tlObjectMapper.get();
        if (objectMapper == null) {
            objectMapper = initObjectMapper();
            tlObjectMapper.set(objectMapper);
        }
        return objectMapper;
    }

    private static ObjectMapper initObjectMapper() {
        return new ObjectMapper();
    }

    public static JsonNode entityToJson(Entity entity) {
        ObjectNode json = getObjectMapper().createObjectNode();
        json.put("_id", entity.getKey().getId());
        json.putPOJO("_kind", entity.getProperty("_kind"));
        json.putPOJO("_account", entity.getProperty("_account"));
        Map<String, Object> props = entity.getProperties();
        for (Map.Entry<String, Object> prop : props.entrySet()) {
            // todo handle proper typing of properties
            json.putPOJO(prop.getKey(), prop.getValue());
        }
        return json;
    }

    public static JsonNode entityListToJson(List<Entity> entityList) throws LeanException {
        ObjectNode json = getObjectMapper().createObjectNode();
        ArrayNode array = getObjectMapper().createArrayNode();

        for (Entity entity : entityList) {
            array.add(entityToJson(entity));
        }
        json.put("result", array);
        return json;
    }
}
