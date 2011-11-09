package com.leanengine.server.appengine;

import com.google.appengine.api.utils.SystemProperty;

public class ServerUtils {

    public static boolean isDevServer(){
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
    }
}
