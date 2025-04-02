package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.TicketMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TicketRepository {
    private final TicketMapper mapper;
    private final DataSource dataSource;

    public TicketRepository(TicketMapper mapper, DataSource dataSource) {
        this.mapper = mapper;
        this.dataSource = dataSource;
    }
}
