package com.leanengine.server.auth;

import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;

import java.io.IOException;
import java.util.Map;

public class LeanAccount {

    public long id = 0;
    public String nickName;
    public String providerId;
    public String provider;
    public Map<String, Object> providerProperties;

    public LeanAccount(long id, String nickName, String providerId, String provider, Map<String, Object> providerProperties) {
        this.id = id;
        this.nickName = nickName;
        this.providerId = providerId;
        this.provider = provider;
        this.providerProperties = providerProperties;
    }

    public String toJson() throws LeanException {
        try {
            return JsonUtils.getObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            throw new LeanException(LeanException.Error.ErrorSerializingToJson, "\n\n" + e.getMessage());
        }
    }
}
