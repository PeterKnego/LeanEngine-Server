package com.leanengine.server.entity;

import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeanQuery {
    private final String kind;
    private List<QueryFilter> filters = new ArrayList<QueryFilter>();
    private List<QuerySort> sorts = new ArrayList<QuerySort>();

    public LeanQuery(String kind) {
        this.kind = kind;
    }

    public void addFilter(String property, QueryFilter.FilterOperator operator, Object value) {
        filters.add(new QueryFilter(property, operator, value));
    }

    public void addSort(String property, QuerySort.SortDirection direction) {
        sorts.add(new QuerySort(property, direction));
    }

    public String getKind() {
        return kind;
    }

    public List<QuerySort> getSorts() {
        return sorts;
    }

    public List<QueryFilter> getFilters() {
        return filters;
    }

    public JsonNode toJson() throws LeanException {
        ObjectNode json = JsonUtils.getObjectMapper().createObjectNode();

        return json;
    }

    public static LeanQuery fromJson(String json) throws LeanException {
        return null;
    }
//        ObjectNode jsonNode;
//        try {
//            jsonNode = (ObjectNode) JsonUtils.getObjectMapper().readTree(json);
//        } catch (IOException e) {
//            throw new LeanException(LeanException.Error.QueryJSON);
//        } catch (ClassCastException cce) {
//            throw new LeanException(LeanException.Error.QueryJSON, " Expected JSON object, instead got JSON array.");
//        }
//
//        // get the 'kind' of the query
//        LeanQuery query = new LeanQuery(jsonNode.get("kind").getTextValue());
//        if (query.getKind() == null) {
//            throw new LeanException(LeanException.Error.QueryJSON, " Missing 'kind' property.");
//        }
//
//        // get 'filters'
//        ArrayNode filters;
//        try {
//            filters = (ArrayNode) jsonNode.get("filters");
//        } catch (ClassCastException cce) {
//            throw new LeanException(LeanException.Error.QueryJSON, " Property 'filters' must be a JSON array.");
//        }
//        for (JsonNode filter : filters) {
//            ObjectNode filterNode;
//            try {
//                filterNode = (ObjectNode) filter;
//            } catch (ClassCastException cce) {
//                throw new LeanException(LeanException.Error.QueryJSON, " Property 'filters' must be a JSON array.");
//            }
//            String filterProperty = filterNode.get("property").getTextValue();
//            String filterOperator = filterNode.get("operator").getTextValue();
//            Object filterValue = filterNode.get("value").;
//            query.addFilter(filter);
//        }
//
//
//        // get 'sorts'
//        ArrayNode sorts;
//        try {
//            sorts = (ArrayNode) jsonNode.get("filters");
//        } catch (ClassCastException cce) {
//            throw new LeanException(LeanException.Error.QueryJSON, " Property 'filters' must be a JSON array.");
//        }
//
//        return query;
//    }

}
