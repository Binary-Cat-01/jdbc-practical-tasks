package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerRepositoryTest {
    @InjectMocks
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @Mock
    private MockedStatic<DriverManager> driverManagerMockedStatic;

    @Mock
    private Connection connection;

    @BeforeEach
    void setUp() {
        driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(),
                anyString(), anyString())).thenReturn(connection);
    }

    @Test
    void createPassengerTableIfNotExists_success() throws SQLException {
//        given:
        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

//        when:
        passengerRepository.createPassengerTableIfNotExists();

//        then:
        verify(statement).executeUpdate(anyString());
    }

    @Test
    void createPassengerTableIfNotExists_failed_sqlException() throws SQLException {
//        given:
        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        doThrow(SQLException.class).when(statement).executeUpdate(anyString());

//        when:
        Executable actual = () -> passengerRepository.createPassengerTableIfNotExists();

//        then:
        assertThrows(RuntimeException.class, actual);
    }

    @Test
    void findAll_success() throws SQLException {
//        given:
        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        ResultSet resultSet = mock(ResultSet.class);
        doReturn(resultSet).when(statement).executeQuery(anyString());

        List<Passenger> expectedPassengers = List.of(new Passenger());
        doReturn(expectedPassengers).when(passengerMapper).map(resultSet);

//        when:
        List<Passenger> foundPassengers = passengerRepository.findAll();

//        then:
        assertSame(expectedPassengers, foundPassengers);
    }

    @Test
    void findAll_failed_sqlException() throws SQLException {
//        given:
        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        doThrow(SQLException.class).when(statement).executeQuery(anyString());

//        when:
        Executable actual = () -> passengerRepository.findAll();

//        then:
        assertThrows(RuntimeException.class, actual);
        verify(passengerMapper, never()).map(any());
    }

    @Test
    void findByFullName_success() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        ResultSet resultSet = mock(ResultSet.class);
        doReturn(resultSet).when(preparedStatement).executeQuery();

        List<Passenger> expected = List.of(getPassengerWithoutNullField());
        doReturn(expected).when(passengerMapper).map(resultSet);

        String expectedFirstName = expected.get(0).getFirstName();
        String expectedLastName = expected.get(0).getLastName();

//        when:
        List<Passenger> found =
                passengerRepository.findByFullName(expectedFirstName, expectedLastName);

//        then:
        assertEquals(expectedFirstName, found.get(0).getFirstName());
        assertEquals(expectedLastName, found.get(0).getLastName());

        verify(preparedStatement).setString(0, expectedFirstName);
        verify(preparedStatement).setString(1, expectedLastName);
    }

    @Test
    void findByFullName_failed_sqlException() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when:
        Executable actual = () -> passengerRepository.findByFullName("first_name", "last_name");

//        then:
        assertThrows(RuntimeException.class, actual);
        verify(passengerMapper, never()).map(any());
    }

    @Test
    void findById_success() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        ResultSet resultSet = mock(ResultSet.class);
        doReturn(resultSet).when(preparedStatement).executeQuery();

        List<Passenger> expected = List.of(getPassengerWithoutNullField());
        doReturn(expected).when(passengerMapper).map(resultSet);

        Long expectedId = expected.get(0).getId();

//        when:
        List<Passenger> found = passengerRepository.findById(expectedId);

//        then:
        assertEquals(expectedId, found.get(0).getId());
        verify(preparedStatement).setLong(0, expectedId);
    }

    @Test
    void findById_failed_sqlException() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when:
        Executable actual = () -> passengerRepository.findById(1L);

//        then:
        assertThrows(RuntimeException.class, actual);
        verify(passengerMapper, never()).map(any());
    }

    @Test
    void findByMale_success() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        ResultSet resultSet = mock(ResultSet.class);
        doReturn(resultSet).when(preparedStatement).executeQuery();

        List<Passenger> expected = List.of(getPassengerWithoutNullField());
        doReturn(expected).when(passengerMapper).map(resultSet);

        Boolean expectedIsMale = expected.get(0).isMale();

//        when:
        List<Passenger> found = passengerRepository.findByMale(expectedIsMale);

//        then:
        assertEquals(expectedIsMale, found.get(0).isMale());
        verify(preparedStatement).setBoolean(1, expectedIsMale);
    }

    @Test
    void findByMale_failed_sqlException() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when:
        Executable actual = () -> passengerRepository.findByMale(true);

//        then:
        assertThrows(RuntimeException.class, actual);
        verify(passengerMapper, never()).map(any());
    }

    @Test
    void findByBirthDate_success() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        ResultSet resultSet = mock(ResultSet.class);
        doReturn(resultSet).when(preparedStatement).executeQuery();

        List<Passenger> expected = List.of(getPassengerWithoutNullField());
        doReturn(expected).when(passengerMapper).map(resultSet);

        LocalDate expectedBirthDate = expected.get(0).getBirthDate();

//        when:
        List<Passenger> found = passengerRepository.findByBirthDate(expectedBirthDate);

//        then:
        assertEquals(expectedBirthDate, found.get(0).getBirthDate());
        verify(preparedStatement).setDate(1, Date.valueOf(expectedBirthDate));
    }

    @Test
    void findByBirthDate_failed_sqlException() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

//        when:
        Executable actual = () -> passengerRepository.findByBirthDate(LocalDate.now());

//        then:
        assertThrows(RuntimeException.class, actual);
        verify(passengerMapper, never()).map(any());
    }

    /*Этот уже тот случай, когда мы построчно сверяем поведение с реализацией или
    * для этого нужно еще отдельно проверить вызовы с конкретными параметрами
    * для каждого тестового пассажира? Пока меня ставят в тупик методы в которых мы
    * можем проверить только поведение c помощью verify. Получается либо слишком подробно,
    * либо не очень полезно.*/
    @Test
    void add_success() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        List<Passenger> expected =
                List.of(getPassengerWithoutNullField(), getPassengerWithNullLastPurchase());

//        when:
        passengerRepository.add(expected);

//        then:
        verify(preparedStatement, times(expected.size())).setLong(eq(1), anyLong());
        verify(preparedStatement, times(expected.size())).setString(eq(2), anyString());
        verify(preparedStatement, times(expected.size())).setString(eq(3), anyString());
        verify(preparedStatement, times(expected.size())).setDate(eq(4), any(Date.class));
        verify(preparedStatement, times(expected.size())).setBoolean(eq(5), anyBoolean());

        /*По бизнес-логике поле lastPurchase может содержать null, поэтому
         *используем ArgumentMatcher - nullable*/
        verify(preparedStatement,
                times(expected.size())).setTimestamp(eq(6), nullable(Timestamp.class));

        verify(preparedStatement, times(expected.size())).executeUpdate();
    }

    @Test
    void add_failed_sqlException() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        doThrow(SQLException.class).when(preparedStatement).executeUpdate();

        List<Passenger> expected =
                List.of(getPassengerWithoutNullField(), getPassengerWithNullLastPurchase());

//        when:
        Executable actual = () -> passengerRepository.add(expected);

//        then:
        assertThrows(RuntimeException.class, actual);
    }

    @Test
    void update_success() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        List<Passenger> expected =
                List.of(getPassengerWithoutNullField(), getPassengerWithNullLastPurchase());

//        when:
        passengerRepository.update(expected);

//        then:
        verify(preparedStatement, times(expected.size())).setString(eq(1), anyString());
        verify(preparedStatement, times(expected.size())).setString(eq(2), anyString());
        verify(preparedStatement, times(expected.size())).setDate(eq(3), any(Date.class));
        verify(preparedStatement, times(expected.size())).setBoolean(eq(4), anyBoolean());

        /*По бизнес-логике поле lastPurchase может содержать null, поэтому
         *используем ArgumentMatcher - nullable*/
        verify(preparedStatement,
                times(expected.size())).setTimestamp(eq(5), nullable(Timestamp.class));

        verify(preparedStatement, times(expected.size())).setLong(eq(6), anyLong());

        verify(preparedStatement, times(expected.size())).executeUpdate();
    }

    @Test
    void update_failed_sqlException() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        doThrow(SQLException.class).when(preparedStatement).executeUpdate();

        List<Passenger> expected =
                List.of(getPassengerWithoutNullField(), getPassengerWithNullLastPurchase());

//        when:
        Executable actual = () -> passengerRepository.update(expected);

//        then:
        assertThrows(RuntimeException.class, actual);
    }

    @Test
    void deleteAll_success() throws SQLException {
//        given:
        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

//        when:
        passengerRepository.deleteAll();

//        then:
        verify(statement).executeUpdate(anyString());
    }

    @Test
    void deleteAll_failed_sqlException() throws SQLException {
//        given:
        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        doThrow(SQLException.class).when(statement).executeUpdate(anyString());

//        when:
        Executable actual = () -> passengerRepository.deleteAll();

//        then:
        assertThrows(RuntimeException.class, actual);
    }

    @Test
    void delete_success() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        List<Passenger> expected =
                List.of(getPassengerWithoutNullField(), getPassengerWithNullLastPurchase());

//        when:
        passengerRepository.delete(expected);

//        then:
        verify(preparedStatement, times(expected.size())).setLong(eq(1), anyLong());

        verify(preparedStatement, times(expected.size())).executeUpdate();
    }

    @Test
    void delete_failed_sqlException() throws SQLException {
//        given:
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        doThrow(SQLException.class).when(preparedStatement).executeUpdate();

        List<Passenger> expected =
                List.of(getPassengerWithoutNullField(), getPassengerWithNullLastPurchase());

//        when:
        Executable actual = () -> passengerRepository.delete(expected);

//        then:
        assertThrows(RuntimeException.class, actual);
    }

    Passenger getPassengerWithoutNullField() {
        Passenger passenger = new Passenger();

        passenger.setId(1L);
        passenger.setFirstName("first_name");
        passenger.setLastName("last_name");
        passenger.setBirthDate(LocalDate.now());
        passenger.setMale(true);
        passenger.setLastPurchase(LocalDateTime.now());

        return passenger;
    }

    Passenger getPassengerWithNullLastPurchase() {
        Passenger passenger = new Passenger();

        passenger.setId(2L);
        passenger.setFirstName("another_first_name");
        passenger.setLastName("another_last_name");
        passenger.setBirthDate(LocalDate.now());
        passenger.setMale(false);
        passenger.setLastPurchase(null);

        return passenger;
    }
}
