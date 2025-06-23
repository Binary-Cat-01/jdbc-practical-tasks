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
        try (Connection connection = dataSource.getConnection()) {

            return existsById(connection, id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при запросе пассажира с id = %s".formatted(id), e);
        }
    }

    public boolean existsById(Connection connection, Long id) {
        String sql = "select from passenger where id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при запросе пассажира с id = %s".formatted(id), e);
        }
    }

    public Passenger createOrUpdate(Connection connection, Passenger passenger) {
        return existsById(connection, passenger.getId()) ?
                updateLastPurchase(connection, passenger) :
                create(connection, passenger);
    }

    public Passenger create(Passenger passenger) {
        try (Connection connection = dataSource.getConnection()) {

            create(connection, passenger);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при создании пассажира '%s'".formatted(passenger), e);
        }

        return passenger;
    }

    public Passenger create(Connection connection, Passenger passenger) {
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

        return passenger;
    }

    public Passenger updateLastPurchase(Connection connection, Passenger passenger) {
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

        return passenger;
    }
}
