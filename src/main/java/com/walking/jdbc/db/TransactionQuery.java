package com.walking.jdbc.db;

import java.sql.Connection;

public class TransactionQuery<T> {
    private final T data;
    private final Query<T> query;

    public TransactionQuery(T data, Query<T> query) {
        this.data = data;
        this.query = query;
    }

    public T getData() {
        return data;
    }

    public Query<T> getQuery() {
        return query;
    }

    public void executeOn(Connection connection) {
        query.execute(connection, data);
    }
}
