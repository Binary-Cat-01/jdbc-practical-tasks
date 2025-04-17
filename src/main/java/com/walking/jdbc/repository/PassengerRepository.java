package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;

import javax.sql.DataSource;
import java.sql.*;

public class PassengerRepository {
    private final PassengerMapper mapper;
    private final DataSource dataSource;

    public PassengerRepository(PassengerMapper mapper, DataSource dataSource) {
        this.mapper = mapper;
        this.dataSource = dataSource;
    }

    public boolean existsById(Long id) {
        String sql = "select from passenger where id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при запросе пассажира с id = %s".formatted(id), e);
        }
    }

    public Passenger create(Passenger passenger) {
        try (Connection connection = dataSource.getConnection()) {

            createWith(connection, passenger);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при создании пассажира '%s'".formatted(passenger), e);
        }

        return passenger;
    }

    public void createTransactional(Connection connection, Object passenger) {
        if (!Passenger.class.equals(passenger.getClass())) {
            throw new IllegalArgumentException(
                    "Объект '%s' должен принадлежать типу '%s', но принадлежит типу '%s'"
                            .formatted(passenger, Passenger.class, passenger.getClass()));
        }

        createWith(connection, (Passenger) passenger);
    }

    public void updateLastPurchaseTransactional(Connection connection, Object passenger) {
        if (!Passenger.class.equals(passenger.getClass())) {
            throw new IllegalArgumentException(
                    ("Объект '%s' должен принадлежать типу '%s', но принадлежит типу '%s'")
                            .formatted(passenger, Passenger.class, passenger.getClass()));
        }

        updateLastPurchaseWith(connection, (Passenger) passenger);
    }

    private void createWith(Connection connection, Passenger passenger) {
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
            throw new RuntimeException(
                    "Ошибка при создании пассажира '%s'".formatted(passenger), e);
        }
    }

    private void updateLastPurchaseWith(Connection connection, Passenger passenger) {
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
            throw new RuntimeException("Ошибка при обновлении последней покупки пассажира '%s'"
                            .formatted(passenger), e);
        }
    }
}
