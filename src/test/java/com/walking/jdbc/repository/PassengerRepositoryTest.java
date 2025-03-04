package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PassengerRepositoryTest {
    private static PassengerMapper passengerMapper;
    private static MockedStatic<DriverManager> driverManagerMockedStatic;
    private Connection connection;
    private Statement statement;
    private PassengerRepository passengerRepository;

    @BeforeAll
    static void beforeAll() {
        passengerMapper = Mockito.mock(PassengerMapper.class);

        driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class);
    }

    @BeforeEach
    void setUp() throws SQLException {
        passengerRepository = new PassengerRepository(passengerMapper);

        initDataBaseMock();
    }

    @Test
    void createPassengerTableIfNotExists_success() throws SQLException {
//        given:
        String validSql = """
                    create table if not exists passenger (
                      id                  bigserial       primary key,
                      first_name          varchar(100)    not null,
                      last_name           varchar(100)    not null,
                      birth_date          date            not null,
                      male                boolean         not null      default true,
                      last_purchase       timestamp,
                    );
                    """;

//        when:
        passengerRepository.createPassengerTableIfNotExists();
//        then:
        Mockito.verify(statement)
               .executeUpdate(validSql);
    }

    @Test
    void createPassengerTableIfNotExists_fail() throws SQLException {
//        given:
        Mockito.doThrow(RuntimeException.class).when(statement).executeUpdate(Mockito.anyString());
//        when:
        Executable actual = () -> passengerRepository.createPassengerTableIfNotExists();
//        then:
        assertThrows(RuntimeException.class, actual);
    }

    @Test
    void deleteAllPassengers_success() throws SQLException {
//        given:
        String validSql = """
                delete from passenger
                """;
//        when:
        passengerRepository.deleteAllPassengers();
//        then:
        Mockito.verify(statement).executeUpdate(validSql);
    }

    @Test
    void deleteAllPassengers_fail() throws SQLException {
//        given:
        Mockito.doThrow(RuntimeException.class).when(statement).executeUpdate(Mockito.anyString());
//        when:
        Executable actual = () -> passengerRepository.deleteAllPassengers();
//        then:
        assertThrows(RuntimeException.class, actual);
    }

    @Test
    void findAll_success() throws SQLException {
//        given:
        List<Passenger> passengerList;

        String validSql = """
                select * from passenger
                """;

        Mockito.doReturn(List.of(Mockito.mock(Passenger.class))).when(passengerMapper).map(Mockito.any());
//        when:
        passengerList = passengerRepository.findAll();
//        then:
        Mockito.verify(statement).executeQuery(validSql);

        assertEquals(1, passengerList.size());
    }

    @Test
    void findAll_fail() throws SQLException {
//        given:
        Mockito.doThrow(RuntimeException.class).when(statement).executeQuery(Mockito.anyString());
//        when:
        Executable actual = () -> passengerRepository.findAll();
//        then:
        assertThrows(RuntimeException.class, actual);

        Mockito.verify(passengerMapper, Mockito.never()).map(Mockito.any());
    }

    private void initDataBaseMock() throws SQLException {
        connection = Mockito.mock(Connection.class);
        statement = Mockito.mock(Statement.class);

        driverManagerMockedStatic.when(() -> DriverManager.getConnection(Mockito.anyString(),
                                         Mockito.anyString(), Mockito.anyString()))
                                 .thenReturn(connection);

        Mockito.doReturn(statement).when(connection).createStatement();
    }
}