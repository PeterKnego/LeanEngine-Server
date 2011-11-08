package com.leanengine.server.rest;

import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
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
        } finally {
            Context.exit();
        }
    }

    @POST
    @Path("/{scriptName}/{property1}")
    public String scriptPOST(@PathParam("scriptName") String scriptName, String property1, String jsonObject) {
        return jsonObject;
    }

    private JsonNode toJSON(NativeObject object) {
        if (object == null) return null;

        ObjectNode result = JsonUtils.getObjectMapper().createObjectNode();
        for (Map.Entry<Object, Object> entry : object.entrySet()) {
            if (entry.getValue().getClass().equals(NativeObject.class)) {
                result.put((String) entry.getKey(), toJSON((NativeObject) entry.getValue()));
            } else if (entry.getValue().getClass().equals(NativeArray.class)) {
                result.put((String) entry.getKey(), toJSON((NativeArray) entry.getValue()));
            } else {
                result.putPOJO((String) entry.getKey(), entry.getValue());
            }

        }

        return result;
    }

    private ArrayNode toJSON(NativeArray array) {
        ArrayNode result = JsonUtils.getObjectMapper().createArrayNode();
        for (Object o : array) {
            if (o.getClass().equals(NativeObject.class)) {
                result.add(toJSON((NativeObject) o));
            } else {
                result.addPOJO(o);
            }
        }
        return result;
    }


}
