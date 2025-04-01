package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.TicketMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TicketRepository {
    private final TicketMapper mapper;

    public TicketRepository(TicketMapper mapper) {
        this.mapper = mapper;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/test_db",
                "postgres",
                "postgres");
    }
}
