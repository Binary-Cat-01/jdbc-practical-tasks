package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PassengerRepository {

    private final DataSource datasource;
    private final PassengerMapper mapper;

    public PassengerRepository(DataSource datasource, PassengerMapper mapper) {
        this.datasource = datasource;
        this.mapper = mapper;
    }

    public Optional<Passenger> findById(Long id) {
        try (Connection connection = datasource.getConnection()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    public Optional<Passenger> findById(Long id, Connection connection) {
        String sql = "select * from passenger where id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    public List<Passenger> findByMale(boolean male) {
        String sql = "select * from passenger where male = ?";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, male);
            ResultSet result = statement.executeQuery();

            return mapper.mapMany(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    public List<Passenger> findByBirthDate(LocalDate birthDate) {
        String sql = "select * from passenger where birth_date = ?";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(birthDate));
            ResultSet result = statement.executeQuery();

            return mapper.mapMany(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    public List<Passenger> findByFullName(String firstName, String lastName) {
        String sql = "select * from passenger where first_name = ? and last_name = ?";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);

            ResultSet result = statement.executeQuery();

            return mapper.mapMany(result);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    public List<Passenger> findAll() {
        try (Connection connection = datasource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("select * from passenger");

            return mapper.mapMany(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    public Passenger create(Passenger passenger) {
        try (Connection connection = datasource.getConnection()) {
            return create(passenger, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании пассажиров", e);
        }
    }

    public Passenger create(Passenger passenger, Connection connection) {
        String sql = "insert into passenger (first_name, last_name, birth_date) values (?, ?, ?) returning id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(passenger, statement);
            ResultSet result = statement.executeQuery();

            result.next();
            passenger.setId(result.getLong(1));

            return passenger;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании пассажиров", e);
        }
    }

    public void createMany(Collection<Passenger> passengers) {
        if (passengers.isEmpty()) {
            return;
        }

        String sql = "insert into passenger (first_name, last_name, birth_date) values (?, ?, ?)";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (var passenger : passengers) {
                setParameters(passenger, statement);
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании пассажиров", e);
        }
    }

    public Passenger update(Passenger passenger) {
        try (Connection connection = datasource.getConnection()) {
            return update(passenger, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании пассажиров", e);
        }
    }

    public Passenger update(Passenger passenger, Connection connection) {
        String sql = """
                update passenger
                    set first_name = ?,
                        last_name = ?,
                        birth_date = ?,
                        last_purchase = ?
                    where id = ?""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(passenger, statement);
            statement.setLong(4, passenger.getId());
            statement.executeUpdate();

            return passenger;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пассажиров", e);
        }
    }

    public void updateMany(Collection<Passenger> passengers) {
        if (passengers.isEmpty()) {
            return;
        }

        String sql = """
                update passenger
                    set first_name = ?,
                        last_name = ?,
                        birth_date = ?,
                        last_purchase = ?
                    where id = ?""";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (var passenger : passengers) {
                setParameters(passenger, statement);
                statement.setLong(4, passenger.getId());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пассажиров", e);
        }
    }

    public void deleteById(Long id) {
        String sql = "delete from passenger where id = ?";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пассажиров", e);
        }
    }

    public void deleteAll() {
        try (Connection connection = datasource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("delete from passenger");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пассажиров", e);
        }
    }

    private void setParameters(Passenger passenger, PreparedStatement statement) throws SQLException {
        statement.setString(1, passenger.getFirstName());
        statement.setString(2, passenger.getLastName());

        Date birthDate = Date.valueOf(passenger.getBirthDate());
        statement.setDate(3, birthDate);
    }
}
