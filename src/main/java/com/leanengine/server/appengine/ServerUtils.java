package com.leanengine.server.appengine;

import org.codehaus.jackson.map.ObjectMapper;

public class ServerUtils {

    private static ThreadLocal<ObjectMapper> tlObjectMapper = new ThreadLocal<ObjectMapper>();

    /**
     * Returns a thread-local instance of JSON ObjectMapper.
     * @return
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
}
