package com.walking.jdbc.mapper;

import com.walking.jdbc.model.Ticket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketMapper {

    public List<Ticket> map(ResultSet resultSet) throws SQLException {
        var tickets = new ArrayList<Ticket>();

        while (resultSet.next()) {
            tickets.add(mapRow(resultSet));
        }

        return tickets;
    }

    private Ticket mapRow(ResultSet resultSet) throws SQLException {
        var ticket = new Ticket();

        ticket.setId(resultSet.getLong("id"));
        ticket.setPassengerId(resultSet.getLong("passenger_id"));
        ticket.setFlightId(resultSet.getLong("flight_id"));

        var lastPurchase = resultSet.getTimestamp("last_purchase");
        ticket.setPurchaseDate(lastPurchase.toLocalDateTime());

        return ticket;
    }
}
