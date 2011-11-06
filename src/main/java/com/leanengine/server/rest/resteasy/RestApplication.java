package com.leanengine.server.rest.resteasy;

import com.leanengine.server.rest.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class RestApplication extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    private static final Logger log = Logger.getLogger(RestApplication.class.getName());


    public RestApplication() {

        singletons.add(new RestSecurityInterceptor());

        singletons.add(new RestExceptionMapper());
        singletons.add(new EntityRest());
        singletons.add(new PublicServiceRest());
        singletons.add(new QueryRest());
        singletons.add(new ScriptRest());

    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
