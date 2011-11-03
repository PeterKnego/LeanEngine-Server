package com.leanengine.server.rest;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QuerySort;
import org.codehaus.jackson.JsonNode;

import javax.ws.rs.*;
import java.util.List;

@Path("/v1/query")
@Produces("application/json")
@Consumes("application/json")
public class QueryRest {

    @POST
    @Path("/")
    public JsonNode query(String queryJson) throws LeanException {
        List<Entity> result = DatastoreUtils.queryEntityPrivate(LeanQuery.fromJson(queryJson));
        return JsonUtils.entityListToJson(result);
    }

    @GET
    @Path("/example")
    public LeanQuery exampleQuery() throws LeanException {
        LeanQuery query = new LeanQuery("somekind");
        query.addFilter("prop1", QueryFilter.FilterOperator.EQUAL, "value1");
        query.addFilter("prop2", QueryFilter.FilterOperator.NOT_EQUAL, "not");
        query.addFilter("prop3", QueryFilter.FilterOperator.LESS_THAN_OR_EQUAL, 1.23);
        query.addFilter("prop4", QueryFilter.FilterOperator.IN, "inside");
        query.addSort("prop2", QuerySort.SortDirection.ASCENDING);
        return query;
    }
}
