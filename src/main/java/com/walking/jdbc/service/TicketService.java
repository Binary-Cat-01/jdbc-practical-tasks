package com.walking.jdbc.service;

import com.walking.jdbc.model.Flight;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.model.Ticket;
import com.walking.jdbc.repository.PassengerRepository;
import com.walking.jdbc.repository.TicketRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class TicketService {
    private final PassengerService passengerService;
    private final TicketRepository ticketRepository;
    private final PassengerRepository passengerRepository;
    private final DataSource dataSource;

    public TicketService(PassengerService passengerService, TicketRepository ticketRepository,
            PassengerRepository passengerRepository, DataSource dataSource) {
        this.passengerService = passengerService;
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
        this.dataSource = dataSource;
    }

    public Ticket purchase(Passenger passenger, Flight flight) {
        Ticket ticket = new Ticket();

        ticket.setId(ticketRepository.getNextId());
        ticket.setPassengerId(passenger.getId());
        ticket.setFlightId(flight.getId());

        LocalDateTime purchaseDate = LocalDateTime.now();

        ticket.setPurchaseDate(purchaseDate);

        passengerService.changeLastPurchase(passenger, purchaseDate);

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                boolean existsPassenger = passengerRepository.existsById(passenger.getId());

                if (existsPassenger) {
                    updateLastPurchase(connection, passenger);
                } else {
                    insertPassenger(connection, passenger);
                }

                insertTicket(connection, ticket);

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при покупке билета", e);
        }

        return ticket;
    }

    private void insertTicket(Connection connection, Ticket ticket) {
        String sql = """
                insert into ticket
                (id, passenger_id, flight_id, purchase_date) values
                (?, ?, ?, ?)
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, ticket.getId());
            preparedStatement.setLong(2, ticket.getPassengerId());
            preparedStatement.setLong(3, ticket.getFlightId());
            preparedStatement.setTimestamp(
                    4,Timestamp.valueOf(ticket.getPurchaseDate()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении билета = %s".formatted(ticket), e);
        }
    }

    private void insertPassenger(Connection connection, Passenger passenger) {
        String sql = """
                insert into passenger
                (id, first_name, last_name, birth_date, male, last_purchase) values
                (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, passenger.getId());
            preparedStatement.setString(2, passenger.getFirstName());
            preparedStatement.setString(3, passenger.getLastName());
            preparedStatement.setDate(4, Date.valueOf(passenger.getBirthDate()));
            preparedStatement.setBoolean(5, passenger.isMale());
            preparedStatement.setTimestamp(
                    6, Timestamp.valueOf(passenger.getLastPurchase()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении пассажира = %s".formatted(passenger),
                    e);
        }
    }

    private void updateLastPurchase(Connection connection, Passenger passenger) {
        String sql = """
                update passenger set
                last_purchase = ?
                where id = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(
                    1, Timestamp.valueOf(passenger.getLastPurchase()));

            preparedStatement.setLong(2, passenger.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пассажира = %s".formatted(passenger),
                    e);
        }
    }
}
