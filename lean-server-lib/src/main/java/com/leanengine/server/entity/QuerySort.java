package com.leanengine.server.entity;

import com.google.appengine.api.datastore.Query;
import com.leanengine.server.LeanException;

public class QuerySort {
    private String property;
    private SortDirection direction;

    public QuerySort(String property, SortDirection direction) {
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

        public String toJSON() {
            return sortString;
        }
    }
}
