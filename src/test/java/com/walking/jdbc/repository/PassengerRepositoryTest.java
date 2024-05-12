package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerRepositoryTest {
    @Mock
    private DataSource datasource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private PassengerMapper mapper;

    @InjectMocks
    private PassengerRepository passengerRepository;


    @Test
    void findById_value_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        Optional<Passenger> passengerOptional = Optional.of(mock(Passenger.class));
        doReturn(passengerOptional).when(mapper).map(rs);

//        when
        var actual = passengerRepository.findById(1L);

//        then
        assertSame(passengerOptional, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void findById_empty_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        Optional<Passenger> passengerOptional = Optional.empty();
        doReturn(passengerOptional).when(mapper).map(rs);

//        when
        var actual = passengerRepository.findById(1L);

//        then
        assertSame(passengerOptional, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void findById_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> passengerRepository.findById(1L);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).executeQuery();
        verify(mapper, never()).map(any());
    }

    @Test
    void findById_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when
        Executable executable = () -> passengerRepository.findById(1L);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper, never()).map(any());
    }

    @Test
    void findById_mappingError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        doThrow(SQLException.class).when(mapper).map(rs);

//        when
        Executable executable = () -> passengerRepository.findById(1L);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void findByIdWithConnection_value_success() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        Optional<Passenger> passengerOptional = Optional.of(mock(Passenger.class));
        doReturn(passengerOptional).when(mapper).map(rs);

//        when
        var actual = passengerRepository.findById(1L, connection);

//        then
        assertSame(passengerOptional, actual);

        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void findByIdWithConnection_empty_success() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        Optional<Passenger> passengerOptional = Optional.empty();
        doReturn(passengerOptional).when(mapper).map(rs);

//        when
        var actual = passengerRepository.findById(1L, connection);

//        then
        assertSame(passengerOptional, actual);

        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void findByIdWithConnection_connectionClosed_failed() throws SQLException {
//        given
        doThrow(SQLException.class).when(connection).prepareStatement(any());

//        when
        Executable executable = () -> passengerRepository.findById(1L, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(preparedStatement, never()).executeQuery();
        verify(mapper, never()).map(any());
    }

    @Test
    void findByIdWithConnection_executeError_failed() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());
        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when
        Executable executable = () -> passengerRepository.findById(1L, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(preparedStatement).executeQuery();
        verify(mapper, never()).map(any());
    }

    @Test
    void findByIdWithConnection_mappingError_failed() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        doThrow(SQLException.class).when(mapper).map(rs);

//        when
        Executable executable = () -> passengerRepository.findById(1L, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void findByMale_empty_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        List<Passenger> passengers = List.of();
        doReturn(passengers).when(mapper).mapMany(rs);

//        when
        var actual = passengerRepository.findByMale(true);

//        then
        assertSame(passengers, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findByMale_value_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        List<Passenger> passengers = List.of(mock(Passenger.class));
        doReturn(passengers).when(mapper).mapMany(rs);

//        when
        var actual = passengerRepository.findByMale(true);

//        then
        assertSame(passengers, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findByMale_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> passengerRepository.findByMale(true);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).executeQuery();
        verify(mapper, never()).mapMany(any());
    }

    @Test
    void findByMale_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when
        Executable executable = () -> passengerRepository.findByMale(true);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper, never()).mapMany(any());
    }

    @Test
    void findByMale_mappingError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        doThrow(SQLException.class).when(mapper).mapMany(rs);

//        when
        Executable executable = () -> passengerRepository.findByMale(true);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findByBirthDate_empty_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        List<Passenger> passengers = List.of();
        doReturn(passengers).when(mapper).mapMany(rs);

//        when
        var actual = passengerRepository.findByBirthDate(LocalDate.now());

//        then
        assertSame(passengers, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findByBirthDate_value_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        List<Passenger> passengers = List.of(mock(Passenger.class));
        doReturn(passengers).when(mapper).mapMany(rs);

//        when
        var actual = passengerRepository.findByBirthDate(LocalDate.now());

//        then
        assertSame(passengers, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findByBirthDate_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> passengerRepository.findByBirthDate(LocalDate.now());

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).executeQuery();
        verify(mapper, never()).mapMany(any());
    }

    @Test
    void findByBirthDate_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when
        Executable executable = () -> passengerRepository.findByBirthDate(LocalDate.now());

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper, never()).mapMany(any());
    }

    @Test
    void findByBirthDate_mappingError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        doThrow(SQLException.class).when(mapper).mapMany(rs);

//        when
        Executable executable = () -> passengerRepository.findByBirthDate(LocalDate.now());

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findByFullName_empty_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        List<Passenger> passengers = List.of();
        doReturn(passengers).when(mapper).mapMany(rs);

//        when
        var actual = passengerRepository.findByFullName("Ivan", "Ivanov");

//        then
        assertSame(passengers, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findByFullName_value_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        List<Passenger> passengers = List.of(mock(Passenger.class));
        doReturn(passengers).when(mapper).mapMany(rs);

//        when
        var actual = passengerRepository.findByFullName("Ivan", "Ivanov");

//        then
        assertSame(passengers, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findByFullName_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> passengerRepository.findByFullName("Ivan", "Ivanov");

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).executeQuery();
        verify(mapper, never()).mapMany(any());
    }

    @Test
    void findByFullName_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when
        Executable executable = () -> passengerRepository.findByFullName("Ivan", "Ivanov");

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper, never()).mapMany(any());
    }

    @Test
    void findByFullName_mappingError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        doThrow(SQLException.class).when(mapper).mapMany(rs);

//        when
        Executable executable = () -> passengerRepository.findByFullName("Ivan", "Ivanov");

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).mapMany(rs);
    }

    @Test
    void findAll_empty_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();

        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(statement).executeQuery(any());

        List<Passenger> passengers = List.of();
        doReturn(passengers).when(mapper).mapMany(rs);

//        when
        var actual = passengerRepository.findAll();

//        then
        assertSame(passengers, actual);

        verify(datasource).getConnection();
        verify(statement).executeQuery(any());
        verify(mapper).mapMany(rs);
    }

    @Test
    void findAll_value_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();

        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(statement).executeQuery(any());

        List<Passenger> passengers = List.of(mock(Passenger.class));
        doReturn(passengers).when(mapper).mapMany(rs);

//        when
        var actual = passengerRepository.findAll();

//        then
        assertSame(passengers, actual);

        verify(datasource).getConnection();
        verify(statement).executeQuery(any());
        verify(mapper).mapMany(rs);
    }

    @Test
    void findAll_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> passengerRepository.findAll();

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(mapper, never()).mapMany(any());
    }

    @Test
    void findAll_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();

        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        doThrow(SQLException.class).when(statement).executeQuery(any());

//        when
        Executable executable = () -> passengerRepository.findAll();

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(statement).executeQuery(any());
        verify(mapper, never()).mapMany(any());
    }

    @Test
    void findAll_mappingError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();

        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(statement).executeQuery(any());

        doThrow(SQLException.class).when(mapper).mapMany(rs);

//        when
        Executable executable = () -> passengerRepository.findAll();

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(statement).executeQuery(any());
        verify(mapper).mapMany(rs);
    }

    @Test
    void create_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(true).when(rs).next();
        doReturn(1L).when(rs).getLong(1);

        doReturn(rs).when(preparedStatement).executeQuery();

        var passenger = getPassenger();

//        when
        var actual = passengerRepository.create(passenger);

//        then
        assertSame(passenger, actual);
        assertEquals(passenger.getId(), actual.getId());

        verify(datasource).getConnection();
        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setDate(anyInt(), eq(Date.valueOf(passenger.getBirthDate())));
        verify(preparedStatement).executeQuery();
        verify(rs).next();
        verify(rs).getLong(1);
    }

    @Test
    void create_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

        Passenger passenger = getPassenger();

//        when
        Executable executable = () -> passengerRepository.create(passenger);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setString(anyInt(), any());
        verify(preparedStatement, never()).setDate(anyInt(), any());
        verify(preparedStatement, never()).executeQuery();
    }

    @Test
    void create_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

        var passenger = getPassenger();

//        when
        Executable executable = () -> passengerRepository.create(passenger);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setDate(anyInt(), eq(Date.valueOf(passenger.getBirthDate())));
        verify(preparedStatement).executeQuery();
    }

    @Test
    void createWithConnection_success() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(true).when(rs).next();
        doReturn(1L).when(rs).getLong(1);

        doReturn(rs).when(preparedStatement).executeQuery();

        var passenger = getPassenger();

//        when
        var actual = passengerRepository.create(passenger, connection);

//        then
        assertSame(passenger, actual);
        assertEquals(passenger.getId(), actual.getId());

        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setDate(anyInt(), eq(Date.valueOf(passenger.getBirthDate())));
        verify(preparedStatement).executeQuery();
        verify(rs).next();
        verify(rs).getLong(1);
    }

    @Test
    void createWithConnection_connectionClosed_failed() throws SQLException {
//        given
        doThrow(SQLException.class).when(connection).prepareStatement(any());

        Passenger passenger = getPassenger();

//        when
        Executable executable = () -> passengerRepository.create(passenger, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(connection).prepareStatement(any());
        verify(preparedStatement, never()).setString(anyInt(), any());
        verify(preparedStatement, never()).setDate(anyInt(), any());
        verify(preparedStatement, never()).executeQuery();
    }

    @Test
    void createWithConnection_executeError_failed() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

        Passenger passenger = getPassenger();

//        when
        Executable executable = () -> passengerRepository.create(passenger, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setDate(anyInt(), eq(Date.valueOf(passenger.getBirthDate())));
        verify(preparedStatement).executeQuery();
    }

    @Test
    void createMany_emptyList_success() throws SQLException {
        List<Passenger> passengers = List.of();

        Executable executable = () -> passengerRepository.createMany(passengers);

        assertDoesNotThrow(executable);
        verify(datasource, never()).getConnection();
    }

    @Test
    void createMany_notEmptyList_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        var passenger1 = getPassenger();
        var passenger2 = getPassenger();
        var passengers = List.of(passenger1, passenger2);

//        when
        passengerRepository.createMany(passengers);

//        then
        verify(datasource).getConnection();
        verify(preparedStatement, times(4)).setString(anyInt(), isNull());
        verify(preparedStatement, times(2)).setDate(anyInt(), any());
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void createMany_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

        var passenger1 = getPassenger();
        var passenger2 = getPassenger();
        var passengers = List.of(passenger1, passenger2);

//        when
        Executable executable = () -> passengerRepository.createMany(passengers);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setString(anyInt(), isNull());
        verify(preparedStatement, never()).setDate(anyInt(), any());
        verify(preparedStatement, never()).addBatch();
        verify(preparedStatement, never()).executeBatch();
    }

    @Test
    void createMany_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());
        doThrow(RuntimeException.class).when(preparedStatement).executeBatch();

        var passenger1 = getPassenger();
        var passenger2 = getPassenger();
        var passengers = List.of(passenger1, passenger2);

//        when
        Executable executable = () -> passengerRepository.createMany(passengers);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, times(4)).setString(anyInt(), isNull());
        verify(preparedStatement, times(2)).setDate(anyInt(), any());
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void update_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        var passenger = getPassenger(1L);

//        when
        var actual = passengerRepository.update(passenger);

//        then
        assertSame(passenger, actual);
        assertEquals(passenger.getId(), actual.getId());

        verify(datasource).getConnection();
        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setDate(anyInt(), eq(Date.valueOf(passenger.getBirthDate())));
        verify(preparedStatement).setLong(4, passenger.getId());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void update_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

        Passenger passenger = getPassenger(1L);

//        when
        Executable executable = () -> passengerRepository.update(passenger);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setString(anyInt(), any());
        verify(preparedStatement, never()).setDate(anyInt(), any());
        verify(preparedStatement, never()).setLong(anyInt(), anyLong());
        verify(preparedStatement, never()).executeUpdate();
    }

    @Test
    void update_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeUpdate();

        var passenger = getPassenger(1L);

//        when
        Executable executable = () -> passengerRepository.update(passenger);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setDate(anyInt(), eq(Date.valueOf(passenger.getBirthDate())));
        verify(preparedStatement).setLong(4, passenger.getId());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void updateWithConnection_success() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        var passenger = getPassenger(1L);

//        when
        var actual = passengerRepository.update(passenger, connection);

//        then
        assertSame(passenger, actual);
        assertEquals(passenger.getId(), actual.getId());

        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setDate(anyInt(), eq(Date.valueOf(passenger.getBirthDate())));
        verify(preparedStatement).setLong(4, passenger.getId());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void updateWithConnection_connectionClosed_failed() throws SQLException {
//        given
        doThrow(SQLException.class).when(connection).prepareStatement(any());

        var passenger = getPassenger(1L);

//        when
        Executable executable = () -> passengerRepository.update(passenger, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(connection).prepareStatement(any());
        verify(preparedStatement, never()).setString(anyInt(), any());
        verify(preparedStatement, never()).setDate(anyInt(), any());
        verify(preparedStatement, never()).setLong(4, passenger.getId());
        verify(preparedStatement, never()).executeUpdate();
    }

    @Test
    void updateWithConnection_executeError_failed() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeUpdate();

        var passenger = getPassenger(1L);

//        when
        Executable executable = () -> passengerRepository.update(passenger, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setDate(anyInt(), eq(Date.valueOf(passenger.getBirthDate())));
        verify(preparedStatement).setLong(4, passenger.getId());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void updateMany_emptyList_success() throws SQLException {
        List<Passenger> passengers = List.of();

        Executable executable = () -> passengerRepository.updateMany(passengers);

        assertDoesNotThrow(executable);
        verify(datasource, never()).getConnection();
    }

    @Test
    void updateMany_notEmptyList_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        var passenger1 = getPassenger(1L);
        var passenger2 = getPassenger(2L);
        var passengers = List.of(passenger1, passenger2);

//        when
        passengerRepository.updateMany(passengers);

//        then
        verify(datasource).getConnection();
        verify(preparedStatement, times(4)).setString(anyInt(), isNull());
        verify(preparedStatement, times(2)).setDate(anyInt(), any());
        verify(preparedStatement, times(2)).setLong(eq(4), anyLong());
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void updateMany_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

        var passenger1 = getPassenger(1L);
        var passenger2 = getPassenger(2L);
        var passengers = List.of(passenger1, passenger2);

//        when
        Executable executable = () -> passengerRepository.updateMany(passengers);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setString(anyInt(), isNull());
        verify(preparedStatement, never()).setDate(anyInt(), any());
        verify(preparedStatement, never()).addBatch();
        verify(preparedStatement, never()).executeBatch();
    }

    @Test
    void updateMany_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());
        doThrow(RuntimeException.class).when(preparedStatement).executeBatch();

        var passenger1 = getPassenger(1L);
        var passenger2 = getPassenger(2L);
        var passengers = List.of(passenger1, passenger2);

//        when
        Executable executable = () -> passengerRepository.updateMany(passengers);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, times(4)).setString(anyInt(), isNull());
        verify(preparedStatement, times(2)).setDate(anyInt(), any());
        verify(preparedStatement, times(2)).setLong(eq(4), anyLong());
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void deleteById_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

//        when
        passengerRepository.deleteById(1L);

//        then
        verify(datasource).getConnection();
        verify(preparedStatement).setLong(1, 1L);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void deleteById_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> passengerRepository.deleteById(1L);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setLong(anyInt(), anyLong());
        verify(preparedStatement, never()).executeUpdate();
    }

    @Test
    void deleteById_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeUpdate();

//        when
        Executable executable = () -> passengerRepository.deleteById(1L);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).setLong(1, 1L);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void deleteAll_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();

        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

//        when
        passengerRepository.deleteAll();

//        then
        verify(datasource).getConnection();
        verify(statement).executeUpdate(any());
    }

    @Test
    void deleteAll_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> passengerRepository.deleteAll();

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
    }

    @Test
    void deleteAll_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();

        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        doThrow(SQLException.class).when(statement).executeUpdate(any());

//        when
        Executable executable = () -> passengerRepository.deleteAll();

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(statement).executeUpdate(any());
    }

    private Passenger getPassenger() {
        return getPassenger(null);
    }

    private Passenger getPassenger(Long id) {
        Passenger passenger = new Passenger();
        passenger.setId(id);
        passenger.setBirthDate(LocalDate.now());

        return passenger;
    }
}
