package com.walking.jdbc.db;

import java.sql.Connection;

@FunctionalInterface
public interface Query<T> {
    void execute(Connection connection, T data);
}
