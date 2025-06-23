package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.TicketMapper;
import com.walking.jdbc.model.Ticket;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class TicketRepository {
    private final TicketMapper mapper;
    private final DataSource dataSource;

    public TicketRepository(TicketMapper mapper, DataSource dataSource) {
        this.mapper = mapper;
        this.dataSource = dataSource;
    }

    public List<Ticket> findAll() {
        String sql = "select * from ticket";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet result = statement.executeQuery(sql);

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении билетов", e);
        }
    }

    public List<Ticket> findById(Long id) {
        String sql = "select * from ticket where id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            ResultSet result = preparedStatement.executeQuery();

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении билета с id = %s".formatted(id), e);
        }
    }

    public Ticket create(Ticket ticket) {
        try (Connection connection = dataSource.getConnection()) {

            create(connection, ticket);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании билета '%s'".formatted(ticket), e);
        }

        return ticket;
    }

    public Ticket create(Connection connection, Ticket ticket) {
        String sql = """
                insert into ticket
                (id, passenger_id, flight_id, purchase_date) values
                (?, ?, ?, ?)
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, ticket.getId());
            preparedStatement.setLong(2, ticket.getPassengerId());
            preparedStatement.setLong(3, ticket.getFlightId());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(ticket.getPurchaseDate()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании билета '%s'".formatted(ticket), e);
        }

        return ticket;
    }

    //В данном случае мы хотим вставить все записи, а если в процессе возникнет исключение,
    //откатить изменения. Так как обрабатывать частичную вставку мы не планируем, нужно выполнить
    //все запросы в батче транзакционно.
    public void createAll(List<Ticket> tickets) {
        String sql = """
                insert into ticket
                (id, passenger_id, flight_id, purchase_date) values
                (?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            try {
                for (Ticket ticket : tickets) {
                    preparedStatement.setLong(1, ticket.getId());
                    preparedStatement.setLong(2, ticket.getPassengerId());
                    preparedStatement.setLong(3, ticket.getFlightId());
                    preparedStatement.setTimestamp(4, Timestamp.valueOf(ticket.getPurchaseDate()));

                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();

                connection.commit();
            } catch (Exception e) {
                connection.rollback();

                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении билетов '%s'".formatted(tickets), e);
        }
    }
}
