package com.leanengine.server.auth;

import java.util.UUID;

public class AuthToken {

    public String token;
    public long accountID = 0;
    public long timeCreated;

    public AuthToken(long accountID) {
        this.accountID = accountID;
        this.token = UUID.randomUUID().toString();
        this.timeCreated = System.currentTimeMillis();
    }

    public AuthToken(String token, long accountID, long timeCreated) {
        this.token = token;
        this.accountID = accountID;
        this.timeCreated = timeCreated;
    }
}
