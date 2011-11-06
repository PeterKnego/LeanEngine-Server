package com.leanengine.server;

import com.google.appengine.api.datastore.Entity;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.*;

public class JsonUtils {

    private static ThreadLocal<ObjectMapper> tlObjectMapper = new ThreadLocal<ObjectMapper>();

    /**
     * Returns a thread-local instance of JSON ObjectMapper.
     *
     * @return ObjectMapper.
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

    public static ObjectNode entityListToJson(List<Entity> entityList) throws LeanException {
        ObjectNode json = getObjectMapper().createObjectNode();
        ArrayNode array = getObjectMapper().createArrayNode();

        for (Entity entity : entityList) {
            array.add(entityToJson(entity));
        }
        json.put("result", array);
        return json;
    }

    public static JsonNode entityToJson(Entity entity) throws LeanException {
        ObjectNode json = getObjectMapper().createObjectNode();
        json.put("_id", entity.getKey().getId());
        json.putPOJO("_kind", entity.getKind());
        json.putPOJO("_account", entity.getProperty("_account"));
        Map<String, Object> props = entity.getProperties();
        for (Map.Entry<String, Object> prop : props.entrySet()) {
            addTypedNode(json, prop.getKey(), prop.getValue());
        }
        return json;
    }

    public static Map<String, Object> entityPropertiesFromJson(JsonNode jsonNode) throws LeanException {
        Map<String, Object> props = new HashMap<String, Object>(jsonNode.size());

        // must have some properties
        if (jsonNode.size() == 0) throw new LeanException(LeanException.Error.EmptyEntity);

        Iterator<String> fieldNames = jsonNode.getFieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();

            // skip LeanEngine system properties (starting with underscore '_')
            if (field.startsWith("_")) continue;
            JsonNode subNode = jsonNode.get(field);
            props.put(field, propertyFromJson(subNode));
        }
        return props;
    }

    public static Object propertyFromJson(JsonNode node) throws LeanException {

        if (node.isObject()) {
            return typedObjectFromJson((ObjectNode) node);
        } else if (node.isArray()) {
            return typedArrayFromJson((ArrayNode) node);
        } else if (node.isLong()) {
            return node.getLongValue();
        } else if (node.isInt()) {
            return node.getIntValue();
        } else if (node.isDouble()) {
            return node.getDoubleValue();
        } else if (node.isBoolean()) {
            return node.getBooleanValue();
        } else if (node.isTextual()) {
            return node.getTextValue();
        } else {
            throw new LeanException(LeanException.Error.ValueToJSON, " Unknown value node type.");
        }
    }

    private static List<Object> typedArrayFromJson(ArrayNode arrayNode) throws LeanException {
        List<Object> result = new ArrayList<Object>(arrayNode.size());
        for (JsonNode node : arrayNode) {
            result.add(propertyFromJson(node));
        }
        return result;
    }

    private static Object typedObjectFromJson(ObjectNode node) throws LeanException {
        // must have 'type' field
        String type = node.get("type").getTextValue();
        if (type == null) throw new LeanException(LeanException.Error.ValueToJSON, " Missing 'type' field.");

        if ("date".equals(type)) {
            return new Date(getLongFromValueNode("value", node));
        } else if ("geopt".equals(type)) {
            throw new IllegalArgumentException("Value nodes of type 'geopt' are not yet implemented.");
        } else if ("geohash".equals(type)) {
            throw new IllegalArgumentException("Value nodes of type 'geohash' are not yet implemented.");
        } else if ("blob".equals(type)) {
            throw new IllegalArgumentException("Value nodes of type 'blob' are not yet implemented.");
        } else if ("shortblob".equals(type)) {
            throw new IllegalArgumentException("Value nodes of type 'shortblob' are not yet implemented.");
        } else if ("text".equals(type)) {
            throw new IllegalArgumentException("Value nodes of type 'text' are not yet implemented.");
        } else if ("reference".equals(type)) {
            throw new IllegalArgumentException("Value nodes of type 'reference' are not yet implemented.");
        } else {
            //unknown node type
            throw new LeanException(LeanException.Error.ValueToJSON, " Unknown type '" + type + "'.");
        }
    }

    private static long getLongFromValueNode(String fieldName, JsonNode node) throws LeanException {
        return getNodeValue(fieldName, node).getLongValue();
    }

    private static JsonNode getNodeValue(String fieldName, JsonNode node) throws LeanException {
        // must have 'fieldName' field
        JsonNode valueNode = node.get(fieldName);
        if (valueNode == null)
            throw new LeanException(LeanException.Error.ValueToJSON, " Missing '" + fieldName + "' field.");

        return valueNode;
    }

    public static void addTypedNode(ObjectNode node, String key, Object value) throws LeanException {
        if (value instanceof List) {
            List list = (List) value;
            ArrayNode arrayNode = JsonUtils.getObjectMapper().createArrayNode();
            for (Object listItem : list) {
                addTypedValueToArray(arrayNode, listItem);
            }
            node.put(key, arrayNode);
        } else {
            addTypedValue(node, key, value);
        }
    }

    private static void addTypedValueToArray(ArrayNode node, Object value) {
        if (value instanceof Long) {
            node.add((Long) value);
        } else if (value instanceof Double) {
            node.add((Double) value);
        } else if (value instanceof String) {
            node.add((String) value);
        } else if (value instanceof Boolean) {
            node.add((Boolean) value);
        } else if(value instanceof Date){
            node.add(getDateNode((Date) value));
        }
    }

    private static void addTypedValue(ObjectNode node, String key, Object value) {
        if (value instanceof Long) {
            node.put(key, (Long) value);
        } else if (value instanceof Double) {
            node.put(key, (Double) value);
        } else if (value instanceof String) {
            node.put(key, (String) value);
        } else if (value instanceof Boolean) {
            node.put(key, (Boolean) value);
        } else if(value instanceof Date){
            node.put(key, getDateNode((Date) value));
        }
    }

    private static ObjectNode getDateNode(Date date) {
        ObjectNode dateNode = JsonUtils.getObjectMapper().createObjectNode();
        dateNode.put("type", "date");
        dateNode.put("value", date.getTime());
        return dateNode;
    }


}
