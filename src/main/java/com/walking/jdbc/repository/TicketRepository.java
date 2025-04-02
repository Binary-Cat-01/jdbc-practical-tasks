package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.TicketMapper;
import com.walking.jdbc.model.Ticket;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class TicketRepository {
    private final TicketMapper mapper;
    private final DataSource dataSource;

    public TicketRepository(TicketMapper mapper, DataSource dataSource) {
        this.mapper = mapper;
        this.dataSource = dataSource;
    }

    public List<Ticket> findAll() {

    }

    public List<Ticket> findById(Long id) {

    }

    public List<Ticket> findByPassengerId(Long id) {

    }

    public List<Ticket> findByFlightId(Long id) {

    }

    public Ticket add(Ticket ticket) {

    }

    public void addAll(List<Ticket> tickets) {

    }

    public Ticket update(Ticket ticket) {

    }

    public void updateAll(List<Ticket> tickets) {

    }

    public Ticket delete(Ticket ticket) {

    }

    public void deleteAll(List<Ticket> tickets) {

    }
}
