package com.leanengine.server.rest;

import java.util.List;

public class ResultList<T> {

    public List<T> result;

    public ResultList(List<T> result) {
        this.result = result;
    }
}
