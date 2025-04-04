package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;

import java.sql.*;
import java.util.List;

public class PassengerRepository {

    private final PassengerMapper mapper;

    public PassengerRepository(PassengerMapper mapper) {
        this.mapper = mapper;
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

//    Реализация уязвимая для SQL-инъекций:
//    public List<Passenger> findByFullName(String firstName, String lastName) {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
//            String sql = "select * from passenger where first_name = %s and last_name = %s"
//                    .formatted(
//                            "'" + firstName + "'",
//                            "'" + lastName + "'"
//                    );
//
//            ResultSet result = statement.executeQuery(sql);
//
//            return mapper.map(result);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public List<Passenger> findAll() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("select * from passenger");

            return mapper.map(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "select from passenger where id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/test_db",
                "postgres",
                "postgres");
    }
}
