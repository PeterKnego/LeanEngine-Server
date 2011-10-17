package com.leanengine.server.rpc.rest.resteasy;

import com.leanengine.server.rpc.rest.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class RestApplication extends Application {

    private Set<Object> singletons = new HashSet<Object>();


    public RestApplication() {

        singletons.add(new RestSecurityInterceptor());

        singletons.add(new RestExceptionMapper());
        singletons.add(new EntityRest());
        singletons.add(new PublicServiceRest());

    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
