package com.leanengine.server.entity;

import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
//        ArrayNode jsonFilters = JsonUtils.getObjectMapper().createArrayNode();
//        for (QueryFilter filters : filters) {
//            ObjectNode jsonFilter = JsonUtils.getObjectMapper().createObjectNode();
//            jsonFilter.put("property", filters.getProperty());
//            jsonFilter.put("operator", filters.getOperator().toJSON());
//            JsonUtils.addTypedValue(jsonFilter, "value", filters.getValue());
//            jsonFilters.add(jsonFilter);
//        }
//        json.put("filters", jsonFilters);

        ObjectNode jsonFilters = JsonUtils.getObjectMapper().createObjectNode();
        for (QueryFilter filter : filters) {
            ObjectNode jsonFilter;
            if (jsonFilters.has(filter.getProperty())) {
                jsonFilter = (ObjectNode) jsonFilters.get(filter.getProperty());
            } else {
                jsonFilter = JsonUtils.getObjectMapper().createObjectNode();
            }
            JsonUtils.addTypedValue(jsonFilter, filter.getOperator().toJSON(), filter.getValue());
            jsonFilters.put(filter.getProperty(), jsonFilter);
        }
        json.put("filter", jsonFilters);

//        ArrayNode jsonSorts = JsonUtils.getObjectMapper().createArrayNode();
//        for (QuerySort sort : sort) {
//            ObjectNode jsonSort = JsonUtils.getObjectMapper().createObjectNode();
//            jsonSort.put("property", sort.getProperty());
//            jsonSort.put("direction", sort.getDirection().toJSON());
//            jsonSorts.add(jsonSort);
//        }
//        json.put("sort", jsonSorts);

        ObjectNode jsonSorts = JsonUtils.getObjectMapper().createObjectNode();
        for (QuerySort sort : sorts) {
            jsonSorts.put(sort.getProperty(), sort.getDirection().toJSON());
        }
        json.put("sort", jsonSorts);

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
        ObjectNode filters;
        try {
            filters = (ObjectNode) jsonNode.get("filter");
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Property 'filter' must be a JSON object.");
        }
        if (filters != null) {
            Iterator<String> filterIterator = filters.getFieldNames();
            while (filterIterator.hasNext()) {
                String filterProperty = filterIterator.next();
                ObjectNode filter;
                try {
                    filter = (ObjectNode) filters.get(filterProperty);
                } catch (ClassCastException cce) {
                    throw new LeanException(LeanException.Error.QueryJSON, " Filter value must be a JSON object.");
                }
                Iterator<String> operatorIterator = filter.getFieldNames();
                while (operatorIterator.hasNext()) {
                    String operator = operatorIterator.next();
                    Object filterValue = JsonUtils.fromJsonTypedValue(filter.get(operator));
                    query.addFilter(
                            filterProperty,
                            QueryFilter.FilterOperator.create(operator),
                            filterValue);
                }
            }
//
//
//            for (JsonNode filters : filters) {
//                JsonNode property = filters.get("property");
//                if (property == null) throw new LeanException(LeanException.Error.QueryJSON,
//                        " Missing 'property' field in 'filters' JSON object.");
//
//                JsonNode operator = filters.get("operator");
//                if (operator == null) throw new LeanException(LeanException.Error.QueryJSON,
//                        " Missing 'operator' field in 'filters' JSON object.");
//
//                JsonNode value = filters.get("value");
//                if (value == null) throw new LeanException(LeanException.Error.QueryJSON,
//                        " Missing 'value' field in 'filters' JSON object.");
//
//                Object filterValue = JsonUtils.fromJsonTypedValue(value);
//                query.addFilter(
//                        property.getTextValue(),
//                        QueryFilter.FilterOperator.create(operator.getTextValue()),
//                        filterValue);
//            }
        }

        // get 'sort'
        ObjectNode sorts;
        try {
            sorts = (ObjectNode) jsonNode.get("sort");
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Property 'sort' must be a JSON object.");
        }
        if (sorts != null) {
            Iterator<String> sortIterator = sorts.getFieldNames();
            while (sortIterator.hasNext()) {
                String sortProperty = sortIterator.next();
                query.addSort(sortProperty, QuerySort.SortDirection.create(sorts.get(sortProperty).getTextValue()));
            }


//            for (JsonNode sort : sorts) {
//                JsonNode property = sort.get("property");
//                if (property == null) throw new LeanException(LeanException.Error.QueryJSON,
//                        " Missing 'property' field in 'sort' JSON object.");
//
//                JsonNode operator = sort.get("direction");
//                if (operator == null) throw new LeanException(LeanException.Error.QueryJSON,
//                        " Missing 'direction' field in 'sort' JSON object.");
//
//                query.addSort(
//                        property.getTextValue(),
//                        QuerySort.SortDirection.create(operator.getTextValue()));
//            }
        }

        return query;
    }

}
