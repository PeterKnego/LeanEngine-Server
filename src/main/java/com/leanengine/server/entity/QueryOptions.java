package com.leanengine.server.entity;

import org.codehaus.jackson.node.ObjectNode;

public class QueryOptions {
    private Integer limit;
    private Integer offset;
    private Integer prefetchSize;
    private String startCursor;
    private String endCursor;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPrefetchSize() {
        return prefetchSize;
    }

    public void setPrefetchSize(Integer prefetchSize) {
        this.prefetchSize = prefetchSize;
    }

    public String getStartCursor() {
        return startCursor;
    }

    public void setStartCursor(String cursor) {
        this.startCursor = cursor;
    }

    public String getEndCursor() {
        return endCursor;
    }

    public void setEndCursor(String cursor) {
        this.endCursor = cursor;
    }

    public static QueryOptions fromJson(ObjectNode node) {
        QueryOptions options = new QueryOptions();
        if (node.get("startCursor") != null) options.setStartCursor(node.get("startCursor").getTextValue());
        if (node.get("endCursor") != null) options.setEndCursor(node.get("endCursor").getTextValue());
        if (node.get("limit") != null) options.setLimit(node.get("limit").getIntValue());
        if (node.get("offset") != null) options.setOffset(node.get("offset").getIntValue());
        if (node.get("prefetchSize") != null) options.setPrefetchSize(node.get("prefetchSize").getIntValue());
        return options;
    }
}
