package com.walking.jdbc.db;

import java.sql.Connection;

@FunctionalInterface
public interface Transactional {
    void executeTransactional(Connection connection, Object object);
}
