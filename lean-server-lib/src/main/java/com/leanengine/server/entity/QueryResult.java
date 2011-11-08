package com.leanengine.server.entity;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;

import java.util.List;

public class QueryResult {
    private List<Entity> result;
    private Cursor cursor;

    public QueryResult(List<Entity> result, Cursor cursor) {
        this.result = result;
        this.cursor = cursor;
    }

    public List<Entity> getResult() {
        return result;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
