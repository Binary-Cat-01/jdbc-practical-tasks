package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class PassengerRepository {

    private final PassengerMapper mapper;

    public PassengerRepository(PassengerMapper mapper) {
        this.mapper = mapper;
    }

    public void createPassengerTableIfNotExists() {
        String sql = """
                    create table if not exists passenger (
                      id                  bigserial       primary key,
                      first_name          varchar(100)    not null,
                      last_name           varchar(100)    not null,
                      birth_date          date            not null,
                      male                boolean         not null      default true,
                      last_purchase       timestamp,
                    );
                    """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при инициализации таблицы с пассажирами", e);
        }
    }

    public List<Passenger> findAll() {
        String sql = """
                select * from passenger
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet result = statement.executeQuery(sql);

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    //    Реализация защищенная от SQL-инъекций:
    public List<Passenger> findByFullName(String firstName, String lastName) {
        String sql = "select * from passenger where first_name = ? and last_name = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);

            ResultSet result = statement.executeQuery();

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Passenger> findById(Long id) {
        String sql = "select from passenger where id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            ResultSet result = preparedStatement.executeQuery();

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажира по id", e);
        }
    }

    public List<Passenger> findByMale(Boolean male) {
        String sql = "select from passenger where male = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBoolean(1, male);

            ResultSet result = preparedStatement.executeQuery();

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров по полу", e);
        }
    }

    public List<Passenger> findByBirthDate (LocalDate birthDate) {
        String sql = "select from passenger where birth_date = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDate(1, Date.valueOf(birthDate));

            ResultSet result = preparedStatement.executeQuery();

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров по дате рождения", e);
        }
    }

    public void deleteAllPassengers() {
        String sql = """
                    delete from passenger
                    """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пассажиров", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/test_db",
                "postgres",
                "postgres");
    }
}
