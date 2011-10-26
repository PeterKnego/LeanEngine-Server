package com.leanengine.server.entity;

import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.ServerUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class LeanQuery {
    private final String kind;
    private List<QueryFilter> filters = new ArrayList<QueryFilter>();
    private List<QuerySort> sorts = new ArrayList<QuerySort>();

    public LeanQuery(String kind) {
        this.kind = kind;
    }

    @JsonCreator
    protected LeanQuery(
            @JsonProperty("kind") String kind,
            @JsonProperty("filters") List<QueryFilter> filters,
            @JsonProperty("sorts") List<QuerySort> sorts) {
        this.kind = kind;
        this.filters = filters;
        this.sorts = sorts;
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

    public String toJSON() throws LeanException {
       ObjectMapper mapper = ServerUtils.getObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new LeanException(LeanException.Error.QueryJSON, e);
        }
    }

    public static LeanQuery parseJSON(String json) throws LeanException {
        ObjectMapper mapper = ServerUtils.getObjectMapper();
        try {
            return mapper.readValue(json, LeanQuery.class);
        } catch (IOException e) {
            throw new LeanException(LeanException.Error.QueryJSON, e);
        }
    }

}
