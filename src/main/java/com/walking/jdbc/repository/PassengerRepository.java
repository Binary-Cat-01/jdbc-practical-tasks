package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                  last_purchase       timestamp
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при инициализации таблицы с пассажирами", e);
        }
    }

    public List<Passenger> findAll() {
        String sql = "select * from passenger";

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

    public List<Passenger> findByBirthDate(LocalDate birthDate) {
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

    public void add(List<Passenger> passengerList) {
        /*Должны ли мы предполагать возможность получения null и как-то обрабатывать ее или
        * это обязанность вызывающего метода\класса?*/
        if (passengerList == null) {
            return;
        }

        /*Должны ли мы предполагать возможность получения пустой коллекции (что не вызовет ошибок,
        * но является бессмысленным) и как-то обрабатывать ее или это обязанность
        * вызывающего метода\класса?*/
        if (passengerList.isEmpty()) {
            return;
        }

        String sql = """
                insert into passenger
                (id, first_name, last_name, birth_date, male, last_purchase) values
                (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (Passenger passenger : passengerList) {
                addCurrent(passenger, preparedStatement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении пассажиров", e);
        }
    }

    public void update(List<Passenger> passengerList) {
        if (passengerList.isEmpty()) {
            return;
        }

        String sql = """
                update passenger set
                first_name = ?
                last_name = ?
                birth_date = ?
                male = ?
                last_purchase = ?
                where id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (Passenger passenger : passengerList) {
                updateCurrent(passenger, preparedStatement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении данных о пассажирах", e);
        }
    }

    public void deleteAll() {
        String sql = "delete from passenger";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пассажиров", e);
        }
    }

    public void delete(List<Passenger> passengerList) {
        String sql = """
                delete passenger
                where id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (Passenger passenger : passengerList) {
                deleteCurrent(passenger, preparedStatement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении данных о пассажирах", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/test_db",
                "postgres",
                "postgres");
    }

    private void addCurrent(Passenger passenger, PreparedStatement preparedStatement) throws
            SQLException {
        /*При вызове геттеров Passenger, если значение поля будет null,
         * получим NPE. На каком классе лежит ответственность за предотвращение
         * NPE:
         * 1) Класс PassengerRepository в методе (addCurrent), где непосредственно используются
         * потенциальные null-поля Passenger?
         * 2) Класс PassengerRepository в методе (add), который получает извне в качестве
         * параметров объекты Passenger c потенциальными null-объектами?
         * 3) Класс, который создает объекты Passenger (конструктор или фабрика)?
         * 4) Как быть с nullable-полем lastPurchase? Заворачивать в Optional или использовать
         * if(lastPurchase != null), там где оно используется?
         * Такой же вопрос с самим объектом Passenger, какой класс отвечает за отсутствие NPE:
         * 1) класс, который будет формировать List<Passenger> для добавления
         * 2) класс, который извлекает объекты Passenger для обработки (метод add в PassengerRepository)
         * 3) класс, который непосредственно использует объекты Passenger (метод addCurrent в PassengerRepository)
         * */
        preparedStatement.setLong(1, passenger.getId());
        preparedStatement.setString(2, passenger.getFirstName());
        preparedStatement.setString(3, passenger.getLastName());
        preparedStatement.setDate(4, Date.valueOf(passenger.getBirthDate()));
        preparedStatement.setBoolean(5, passenger.isMale());
        preparedStatement.setTimestamp(6, toTimestamp(passenger.getLastPurchase()));

        preparedStatement.executeUpdate();
    }

    private void updateCurrent(Passenger passenger, PreparedStatement preparedStatement)
            throws SQLException {
        preparedStatement.setString(1, passenger.getFirstName());
        preparedStatement.setString(2, passenger.getLastName());
        preparedStatement.setDate(3, Date.valueOf(passenger.getBirthDate()));
        preparedStatement.setBoolean(4, passenger.isMale());
        preparedStatement.setTimestamp(5, toTimestamp(passenger.getLastPurchase()));
        preparedStatement.setLong(6, passenger.getId());

        preparedStatement.executeUpdate();
    }

    private void deleteCurrent(Passenger passenger, PreparedStatement preparedStatement)
            throws SQLException {
        preparedStatement.setLong(1, passenger.getId());

        preparedStatement.executeUpdate();
    }

    private Timestamp toTimestamp(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }
}
