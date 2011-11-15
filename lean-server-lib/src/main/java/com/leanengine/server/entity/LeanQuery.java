package com.leanengine.server.entity;

import com.google.appengine.api.datastore.Cursor;
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
    private QueryOptions queryOptions;
    private Cursor cursor;

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

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public List<QuerySort> getSorts() {
        return sorts;
    }

    public List<QueryFilter> getFilters() {
        return filters;
    }

    public QueryOptions getQueryOptions() {
        return queryOptions;
    }

    public void setQueryOptions(QueryOptions queryOptions) {
        this.queryOptions = queryOptions;
    }

    public JsonNode toJson() throws LeanException {
        ObjectNode json = JsonUtils.getObjectMapper().createObjectNode();
        json.put("kind", kind);

        if (!filters.isEmpty()) {
            ObjectNode jsonFilters = JsonUtils.getObjectMapper().createObjectNode();
            for (QueryFilter filter : filters) {
                ObjectNode jsonFilter;
                if (jsonFilters.has(filter.getProperty())) {
                    jsonFilter = (ObjectNode) jsonFilters.get(filter.getProperty());
                } else {
                    jsonFilter = JsonUtils.getObjectMapper().createObjectNode();
                }
                JsonUtils.addTypedNode(jsonFilter, filter.getOperator().toJSON(), filter.getValue());
                jsonFilters.put(filter.getProperty(), jsonFilter);
            }
            json.put("filter", jsonFilters);
        }

        if (!sorts.isEmpty()) {
            ObjectNode jsonSorts = JsonUtils.getObjectMapper().createObjectNode();
            for (QuerySort sort : sorts) {
                jsonSorts.put(sort.getProperty(), sort.getDirection().toJSON());
            }
            json.put("sort", jsonSorts);
        }

        if (this.cursor != null) {
           json.put("cursor", cursor.toWebSafeString());
        }

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

        // get 'filter'
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
                    Object filterValue = JsonUtils.propertyFromJson(filter.get(operator));
                    query.addFilter(
                            filterProperty,
                            QueryFilter.FilterOperator.create(operator),
                            filterValue);
                }
            }
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
        }

        // get 'options'
        ObjectNode options;
        try {
            options = (ObjectNode) jsonNode.get("options");
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Property 'options' must be a JSON object.");
        }
        if (options != null) {
            query.setQueryOptions(QueryOptions.fromJson(options));
        }

        // get 'cursor'
        JsonNode cursorNode = jsonNode.get("cursor");
        if (cursorNode != null) {
            query.cursor = Cursor.fromWebSafeString(cursorNode.getTextValue());
        }

        return query;
    }

}
