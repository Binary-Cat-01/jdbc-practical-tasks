package com.walking.jdbc.db;

import java.sql.Connection;

@FunctionalInterface
public interface TransactionalExecutor{
    void executeTransactional(Connection connection, Object objectForTransaction);
}
