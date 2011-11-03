package com.leanengine.server.entity;

import com.google.appengine.api.datastore.Query;
import com.leanengine.server.LeanException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;

public class QueryFilter {
    private String property;
    private FilterOperator operator;
    private Object value;

    public QueryFilter(String property, FilterOperator operator, Object value) {

        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    public enum FilterOperator {
        IN("IN",Query.FilterOperator.IN),
        EQUAL("=",Query.FilterOperator.EQUAL),
        GREATER_THAN(">",Query.FilterOperator.GREATER_THAN),
        GREATER_THAN_OR_EQUAL(">=",Query.FilterOperator.GREATER_THAN_OR_EQUAL),
        LESS_THAN("<",Query.FilterOperator.LESS_THAN),
        LESS_THAN_OR_EQUAL("<=",Query.FilterOperator.LESS_THAN_OR_EQUAL),
        NOT_EQUAL("!=",Query.FilterOperator.NOT_EQUAL);

        private String operatorString;
        private Query.FilterOperator gaeOperator;

        @JsonCreator
        static FilterOperator creator(String jsonOperator) throws LeanException {
            if("=".equals(jsonOperator)){
                return FilterOperator.EQUAL;
            } else if(">".equals(jsonOperator)){
                return FilterOperator.GREATER_THAN;
            } else if(">=".equals(jsonOperator)){
                return FilterOperator.GREATER_THAN_OR_EQUAL;
            } else if("<".equals(jsonOperator)){
                return FilterOperator.LESS_THAN;
            } else if("<=".equals(jsonOperator)){
                return FilterOperator.LESS_THAN_OR_EQUAL;
            } else if("!=".equals(jsonOperator)){
                return FilterOperator.NOT_EQUAL;
            } else if("IN".equals(jsonOperator)){
                return FilterOperator.IN;
            }
            throw new LeanException(LeanException.Error.UnsupportedQueryFilterOperation, jsonOperator);
        }

        FilterOperator( String operatorString, Query.FilterOperator gaeOperator) {
            this.operatorString = operatorString;
            this.gaeOperator = gaeOperator;
        }

        public Query.FilterOperator getFilterOperator() {
            return gaeOperator;
        }

        public String toJSON() {
            return operatorString;
        }
    }
}
