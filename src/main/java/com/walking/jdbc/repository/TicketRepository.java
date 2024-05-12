package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.TicketMapper;
import com.walking.jdbc.model.Ticket;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TicketRepository {

    private final DataSource datasource;
    private final TicketMapper mapper;

    public TicketRepository(DataSource datasource, TicketMapper mapper) {
        this.datasource = datasource;
        this.mapper = mapper;
    }

    public Optional<Ticket> findById(Long id) {
        String sql = "select * from ticket where id = ?";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении билетов", e);
        }
    }

    public List<Ticket> findAll() {
        try (Connection connection = datasource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("select * from ticket");

            return mapper.mapMany(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении билетов", e);
        }
    }

    public Ticket create(Ticket ticket) {
        try (Connection connection = datasource.getConnection()) {
            return create(ticket, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании билетов", e);
        }
    }

    public Ticket create(Ticket ticket, Connection connection) {
        String sql = """
                insert into ticket
                    (departure_airport, arrival_airport, departure_date, arrival_date, purchase_date, passenger_id)
                    values (?, ?, ?, ?, ?, ?) returning id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(ticket, statement);
            ResultSet result = statement.executeQuery();

            result.next();
            ticket.setId(result.getLong(1));

            return ticket;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании билетов", e);
        }
    }

    public void createMany(Collection<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return;
        }

        String sql = """
                insert into ticket
                    (departure_airport, arrival_airport, departure_date, arrival_date, purchase_date, passenger_id)
                    values (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (var ticket : tickets) {
                setParameters(ticket, statement);
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании билетов", e);
        }
    }

    public Ticket update(Ticket ticket) {
        String sql = """
                update ticket
                    set departure_airport = ?,
                        arrival_airport = ?,
                        departure_date = ?,
                        arrival_date = ?,
                        purchase_date = ?,
                        passenger_id = ?
                    where id = ?""";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(ticket, statement);
            statement.setLong(7, ticket.getId());
            statement.executeUpdate();

            return ticket;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении билетов", e);
        }
    }

    public void updateMany(Collection<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return;
        }

        String sql = """
                update ticket
                    set departure_airport = ?,
                        arrival_airport = ?,
                        departure_date = ?,
                        arrival_date = ?,
                        purchase_date = ?,
                        passenger_id = ?
                    where id = ?""";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (var ticket : tickets) {
                setParameters(ticket, statement);
                statement.setLong(7, ticket.getId());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении билетов", e);
        }
    }

    public void deleteById(Long id) {
        String sql = "delete from ticket where id = ?";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении билетов", e);
        }
    }

    public void deleteAll() {
        try (Connection connection = datasource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("delete from ticket");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении билетов", e);
        }
    }

    private void setParameters(Ticket ticket, PreparedStatement statement) throws SQLException {
        statement.setString(1, ticket.getDepartureAirport());
        statement.setString(2, ticket.getArrivalAirport());

        var departureDate = Timestamp.valueOf(ticket.getDepartureDate());
        statement.setTimestamp(3, departureDate);

        var arrivalDate = Timestamp.valueOf(ticket.getArrivalDate());
        statement.setTimestamp(4, arrivalDate);

        var purchaseDate = Timestamp.valueOf(ticket.getPurchaseDate());
        statement.setTimestamp(5, purchaseDate);

        statement.setLong(6, ticket.getPassengerId());
    }
}
