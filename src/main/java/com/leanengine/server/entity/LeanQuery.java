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
        json.put("kind", kind);
        ArrayNode jsonFilters = JsonUtils.getObjectMapper().createArrayNode();
        for (QueryFilter filter : filters) {
            ObjectNode jsonFilter = JsonUtils.getObjectMapper().createObjectNode();
            jsonFilter.put("property", filter.getProperty());
            jsonFilter.put("operator", filter.getOperator().toJSON());
            JsonUtils.addTypedValue(jsonFilter, "value", filter.getValue());
            jsonFilters.add(jsonFilter);
        }
        json.put("filters", jsonFilters);

        ArrayNode jsonSorts = JsonUtils.getObjectMapper().createArrayNode();
        ;
        for (QuerySort sort : sorts) {
            ObjectNode jsonSort = JsonUtils.getObjectMapper().createObjectNode();
            jsonSort.put("property", sort.getProperty());
            jsonSort.put("direction", sort.getDirection().toJSON());
            jsonSorts.add(jsonSort);
        }
        json.put("sorts", jsonSorts);

        return json;
    }

    public static LeanQuery fromJson(String json) throws LeanException {
        ObjectNode jsonNode;
        try {
            jsonNode = (ObjectNode) JsonUtils.getObjectMapper().readTree(json);
        } catch (IOException e) {
            throw new LeanException(LeanException.Error.QueryJSON);
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Expected JSON object, instead got JSON array.");
        }

        // get the 'kind' of the query
        LeanQuery query = new LeanQuery(jsonNode.get("kind").getTextValue());
        if (query.getKind() == null) {
            throw new LeanException(LeanException.Error.QueryJSON, " Missing 'kind' property.");
        }

        // get 'filters'
        ArrayNode filters;
        try {
            filters = (ArrayNode) jsonNode.get("filters");
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Property 'filters' must be a JSON array.");
        }
        if (filters != null) {
            for (JsonNode filter : filters) {
                JsonNode property = filter.get("property");
                if (property == null) throw new LeanException(LeanException.Error.QueryJSON,
                        " Missing 'property' field in 'filter' JSON object.");

                JsonNode operator = filter.get("operator");
                if (operator == null) throw new LeanException(LeanException.Error.QueryJSON,
                        " Missing 'operator' field in 'filter' JSON object.");

                JsonNode value = filter.get("value");
                if (value == null) throw new LeanException(LeanException.Error.QueryJSON,
                        " Missing 'value' field in 'filter' JSON object.");

                Object filterValue = JsonUtils.fromJsonTypedValue(value);
                query.addFilter(
                        property.getTextValue(),
                        QueryFilter.FilterOperator.create(operator.getTextValue()),
                        filterValue);
            }
        }

        // get 'sorts'
        ArrayNode sorts;
        try {
            sorts = (ArrayNode) jsonNode.get("sorts");
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Property 'sorts' must be a JSON array.");
        }
        if (sorts != null) {
            for (JsonNode sort : sorts) {
                JsonNode property = sort.get("property");
                if (property == null) throw new LeanException(LeanException.Error.QueryJSON,
                        " Missing 'property' field in 'sorts' JSON object.");

                JsonNode operator = sort.get("direction");
                if (operator == null) throw new LeanException(LeanException.Error.QueryJSON,
                        " Missing 'direction' field in 'sorts' JSON object.");

                query.addSort(
                        property.getTextValue(),
                        QuerySort.SortDirection.create(operator.getTextValue()));
            }
        }

        return query;
    }

}
