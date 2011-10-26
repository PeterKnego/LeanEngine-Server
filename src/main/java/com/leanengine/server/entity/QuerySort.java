package com.leanengine.server.entity;

import com.google.appengine.api.datastore.Query;
import com.leanengine.server.LeanException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;

public class QuerySort {
    private String property;
    private SortDirection direction;

    @JsonCreator
    public QuerySort(
            @JsonProperty("property") String property,
            @JsonProperty("direction") SortDirection direction) {
        this.property = property;
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public enum SortDirection {
        ASCENDING("asc", Query.SortDirection.ASCENDING),
        DESCENDING("desc", Query.SortDirection.DESCENDING);

        private String sortString;
        private Query.SortDirection sortDirection;

        SortDirection(String sortString, Query.SortDirection sortDirection) {
            this.sortString = sortString;
            this.sortDirection = sortDirection;
        }

        @JsonCreator
        public static SortDirection create(String sortJson) throws LeanException {
            if ("asc".equals(sortJson)) {
                return SortDirection.ASCENDING;
            } else if ("desc".equals(sortJson)) {
                return SortDirection.DESCENDING;
            }
            throw new LeanException(LeanException.Error.UnsupportedQuerySortOperation, sortJson);
        }

        public Query.SortDirection getSortDirection() {
            return sortDirection;
        }

        @JsonValue
        public String toJSON() {
            return sortString;
        }
    }
}
