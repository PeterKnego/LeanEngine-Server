package com.leanengine.server;

import com.google.appengine.api.datastore.Key;

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
}
