package com.leanengine.server.rest;

import com.leanengine.server.LeanException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.*;

import javax.ws.rs.*;
import java.util.Map;
import java.util.logging.Logger;

@Path("/v1/script")
@Produces("application/json")
@Consumes("application/json")
public class ScriptRest {

    private static final Logger log = Logger.getLogger(ScriptRest.class.getName());


    @POST
    @Path("/{scriptName}")
    public String scriptPOST(@PathParam("scriptName") String scriptName, String code) throws LeanException {

        //todo resolve Script name
        
        Context ctx = Context.enter();
        try {
            ScriptableObject scope = ctx.initStandardObjects();

            Script script = ctx.compileString(code, "<code>", 1, null);

            Object result = script.exec(ctx, scope);

            if (result == null || !result.getClass().equals(NativeObject.class)) {
                throw new LeanException(LeanException.Error.ScriptOutputError, scriptName);
            }

            return toJSON((NativeObject) result).toString();
        } catch (RhinoException ex) {
            throw new LeanException(LeanException.Error.ScriptExecutionError, scriptName, ex);
        } catch (JSONException e) {
            throw new LeanException(LeanException.Error.ScriptOutputError, scriptName);
        } finally {
            Context.exit();
        }
    }

    @POST
    @Path("/{scriptName}/{property1}")
    public String scriptPOST(@PathParam("scriptName") String scriptName, String property1, String jsonObject) {
        return jsonObject;
    }

    private JSONObject toJSON(NativeObject object) throws JSONException {
        if (object == null) return null;

        JSONObject result = new JSONObject();
        for (Map.Entry<Object, Object> entry : object.entrySet()) {
            if (entry.getValue().getClass().equals(NativeObject.class)) {
                result.put((String) entry.getKey(), toJSON((NativeObject) entry.getValue()));
            } else if (entry.getValue().getClass().equals(NativeArray.class)) {
                result.put((String) entry.getKey(), toJSON((NativeArray) entry.getValue()));
            } else {
                result.put((String) entry.getKey(), entry.getValue());
            }

        }

        return result;
    }

    private JSONArray toJSON(NativeArray array) throws JSONException {
        JSONArray result = new JSONArray();
        for (Object o : array) {
            if (o.getClass().equals(NativeObject.class)) {
                result.put(toJSON((NativeObject) o));
            } else {
                result.put(o);
            }
        }
        return result;
    }


}
