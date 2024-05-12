package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.TicketMapper;
import com.walking.jdbc.model.Ticket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketRepositoryTest {
    private static final long PASSENGER_ID = 3L;

    @Mock
    private DataSource datasource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private TicketMapper mapper;

    @InjectMocks
    private TicketRepository ticketRepository;


    @Test
    void findById_value_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(preparedStatement).executeQuery();

        Optional<Ticket> ticketOptional = Optional.of(mock(Ticket.class));
        doReturn(ticketOptional).when(mapper).map(rs);

//        when
        var actual = ticketRepository.findById(1L);

//        then
        assertSame(ticketOptional, actual);

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

        Optional<Ticket> ticketOptional = Optional.empty();
        doReturn(ticketOptional).when(mapper).map(rs);

//        when
        var actual = ticketRepository.findById(1L);

//        then
        assertSame(ticketOptional, actual);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void findById_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> ticketRepository.findById(1L);

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
        Executable executable = () -> ticketRepository.findById(1L);

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
        Executable executable = () -> ticketRepository.findById(1L);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void findAll_empty_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();

        Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();

        ResultSet rs = mock(ResultSet.class);
        doReturn(rs).when(statement).executeQuery(any());

        List<Ticket> tickets = List.of();
        doReturn(tickets).when(mapper).mapMany(rs);

//        when
        var actual = ticketRepository.findAll();

//        then
        assertSame(tickets, actual);

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

        List<Ticket> tickets = List.of(mock(Ticket.class));
        doReturn(tickets).when(mapper).mapMany(rs);

//        when
        var actual = ticketRepository.findAll();

//        then
        assertSame(tickets, actual);

        verify(datasource).getConnection();
        verify(statement).executeQuery(any());
        verify(mapper).mapMany(rs);
    }

    @Test
    void findAll_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> ticketRepository.findAll();

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
        Executable executable = () -> ticketRepository.findAll();

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
        Executable executable = () -> ticketRepository.findAll();

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

        var dateTime = LocalDateTime.now();
        var ticket = getTicket(dateTime);

//        when
        var actual = ticketRepository.create(ticket);

//        then
        assertSame(ticket, actual);
        assertEquals(ticket.getId(), actual.getId());

        verify(datasource).getConnection();

        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setLong(6, PASSENGER_ID);


        Timestamp timestamp = Timestamp.valueOf(dateTime);
        verify(preparedStatement, times(3)).setTimestamp(anyInt(), eq(timestamp));

        verify(preparedStatement).executeQuery();
        verify(rs).next();
        verify(rs).getLong(1);
    }

    @Test
    void create_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

        var ticket = getTicket(LocalDateTime.now());

//        when
        Executable executable = () -> ticketRepository.create(ticket);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setString(anyInt(), any());
        verify(preparedStatement, never()).setLong(anyInt(), anyLong());
        verify(preparedStatement, never()).setTimestamp(anyInt(), any());
        verify(preparedStatement, never()).executeQuery();
    }

    @Test
    void create_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

        var dateTime = LocalDateTime.now();
        var ticket = getTicket(dateTime);

//        when
        Executable executable = () -> ticketRepository.create(ticket);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setLong(6, PASSENGER_ID);

        Timestamp timestamp = Timestamp.valueOf(dateTime);
        verify(preparedStatement, times(3)).setTimestamp(anyInt(), eq(timestamp));

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

        var dateTime = LocalDateTime.now();
        var ticket = getTicket(dateTime);

//        when
        var actual = ticketRepository.create(ticket, connection);

//        then
        assertSame(ticket, actual);
        assertEquals(ticket.getId(), actual.getId());

        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setLong(6, PASSENGER_ID);

        Timestamp timestamp = Timestamp.valueOf(dateTime);
        verify(preparedStatement, times(3)).setTimestamp(anyInt(), eq(timestamp));

        verify(preparedStatement).executeQuery();
        verify(rs).next();
        verify(rs).getLong(1);
    }

    @Test
    void createWithConnection_connectionClosed_failed() throws SQLException {
//        given
        doThrow(SQLException.class).when(connection).prepareStatement(any());

        var dateTime = LocalDateTime.now();
        var ticket = getTicket(dateTime);

//        when
        Executable executable = () -> ticketRepository.create(ticket, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(connection).prepareStatement(any());
        verify(preparedStatement, never()).setString(anyInt(), any());
        verify(preparedStatement, never()).setLong(anyInt(), anyLong());
        verify(preparedStatement, never()).setTimestamp(anyInt(), any());
        verify(preparedStatement, never()).executeQuery();
    }

    @Test
    void createWithConnection_executeError_failed() throws SQLException {
//        given
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeQuery();

        var dateTime = LocalDateTime.now();
        var ticket = getTicket(dateTime);

//        when
        Executable executable = () -> ticketRepository.create(ticket, connection);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setLong(6, PASSENGER_ID);

        Timestamp timestamp = Timestamp.valueOf(dateTime);
        verify(preparedStatement, times(3)).setTimestamp(anyInt(), eq(timestamp));
        verify(preparedStatement).executeQuery();
    }

    @Test
    void createMany_emptyList_success() throws SQLException {
        List<Ticket> tickets = List.of();

        Executable executable = () -> ticketRepository.createMany(tickets);

        assertDoesNotThrow(executable);
        verify(datasource, never()).getConnection();
    }

    @Test
    void createMany_notEmptyList_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        var dateTime = LocalDateTime.now();
        var ticket1 = getTicket(dateTime);
        var ticket2 = getTicket(dateTime);
        var tickets = List.of(ticket1, ticket2);

//        when
        ticketRepository.createMany(tickets);

//        then
        verify(datasource).getConnection();
        verify(preparedStatement, times(4)).setString(anyInt(), isNull());
        verify(preparedStatement, times(2)).setLong(eq(6), eq(PASSENGER_ID));
        verify(preparedStatement, times(6)).setTimestamp(anyInt(), any());
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void createMany_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

        var dateTime = LocalDateTime.now();
        var ticket1 = getTicket(dateTime);
        var ticket2 = getTicket(dateTime);
        var tickets = List.of(ticket1, ticket2);

//        when
        Executable executable = () -> ticketRepository.createMany(tickets);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setString(anyInt(), isNull());
        verify(preparedStatement, never()).setLong(anyInt(), anyLong());
        verify(preparedStatement, never()).setTimestamp(anyInt(), any());
        verify(preparedStatement, never()).addBatch();
        verify(preparedStatement, never()).executeBatch();
    }

    @Test
    void createMany_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());
        doThrow(RuntimeException.class).when(preparedStatement).executeBatch();

        var dateTime = LocalDateTime.now();
        var ticket1 = getTicket(dateTime);
        var ticket2 = getTicket(dateTime);
        var tickets = List.of(ticket1, ticket2);

//        when
        Executable executable = () -> ticketRepository.createMany(tickets);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, times(4)).setString(anyInt(), isNull());
        verify(preparedStatement, times(2)).setLong(eq(6), eq(PASSENGER_ID));
        verify(preparedStatement, times(6)).setTimestamp(anyInt(), any());
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void update_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        var dateTime = LocalDateTime.now();
        var ticket = getTicket(1L, dateTime);

//        when
        var actual = ticketRepository.update(ticket);

//        then
        assertSame(ticket, actual);
        assertEquals(ticket.getId(), actual.getId());

        verify(datasource).getConnection();
        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setLong(6, PASSENGER_ID);

        Timestamp timestamp = Timestamp.valueOf(dateTime);
        verify(preparedStatement, times(3)).setTimestamp(anyInt(), eq(timestamp));
        verify(preparedStatement).setLong(7, ticket.getId());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void update_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

        var dateTime = LocalDateTime.now();
        Ticket ticket = getTicket(1L, dateTime);

//        when
        Executable executable = () -> ticketRepository.update(ticket);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setString(anyInt(), any());
        verify(preparedStatement, never()).setLong(anyInt(), anyLong());
        verify(preparedStatement, never()).setTimestamp(anyInt(), any());
        verify(preparedStatement, never()).setLong(anyInt(), anyLong());
        verify(preparedStatement, never()).executeUpdate();
    }

    @Test
    void update_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        doThrow(SQLException.class).when(preparedStatement).executeUpdate();

        var dateTime = LocalDateTime.now();
        var ticket = getTicket(1L, dateTime);

//        when
        Executable executable = () -> ticketRepository.update(ticket);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, times(2)).setString(anyInt(), isNull());
        verify(preparedStatement).setLong(6, PASSENGER_ID);

        Timestamp timestamp = Timestamp.valueOf(dateTime);
        verify(preparedStatement, times(3)).setTimestamp(anyInt(), eq(timestamp));

        verify(preparedStatement).setLong(7, ticket.getId());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void updateMany_emptyList_success() throws SQLException {
        List<Ticket> tickets = List.of();

        Executable executable = () -> ticketRepository.updateMany(tickets);

        assertDoesNotThrow(executable);
        verify(datasource, never()).getConnection();
    }

    @Test
    void updateMany_notEmptyList_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

        var dateTime = LocalDateTime.now();
        var ticket1 = getTicket(1L, dateTime);
        var ticket2 = getTicket(2L, dateTime);
        var tickets = List.of(ticket1, ticket2);

//        when
        ticketRepository.updateMany(tickets);

//        then
        verify(datasource).getConnection();
        verify(preparedStatement, times(4)).setString(anyInt(), isNull());
        verify(preparedStatement, times(2)).setLong(eq(6), eq(PASSENGER_ID));
        verify(preparedStatement, times(6)).setTimestamp(anyInt(), any());
        verify(preparedStatement, times(2)).setLong(eq(7), anyLong());
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void updateMany_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

        var dateTime = LocalDateTime.now();
        var ticket1 = getTicket(1L, dateTime);
        var ticket2 = getTicket(2L, dateTime);
        var tickets = List.of(ticket1, ticket2);

//        when
        Executable executable = () -> ticketRepository.updateMany(tickets);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, never()).setString(anyInt(), isNull());
        verify(preparedStatement, never()).setLong(anyInt(), anyLong());
        verify(preparedStatement, never()).setTimestamp(anyInt(), any());
        verify(preparedStatement, never()).addBatch();
        verify(preparedStatement, never()).executeBatch();
    }

    @Test
    void updateMany_executeError_failed() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());
        doThrow(RuntimeException.class).when(preparedStatement).executeBatch();

        var dateTime = LocalDateTime.now();
        var ticket1 = getTicket(1L, dateTime);
        var ticket2 = getTicket(2L, dateTime);
        var tickets = List.of(ticket1, ticket2);

//        when
        Executable executable = () -> ticketRepository.updateMany(tickets);

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(preparedStatement, times(4)).setString(anyInt(), isNull());
        verify(preparedStatement, times(2)).setLong(eq(6), eq(PASSENGER_ID));
        verify(preparedStatement, times(6)).setTimestamp(anyInt(), any());
        verify(preparedStatement, times(2)).setLong(eq(7), anyLong());
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void deleteById_success() throws SQLException {
//        given
        doReturn(connection).when(datasource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any());

//        when
        ticketRepository.deleteById(1L);

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
        Executable executable = () -> ticketRepository.deleteById(1L);

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
        Executable executable = () -> ticketRepository.deleteById(1L);

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
        ticketRepository.deleteAll();

//        then
        verify(datasource).getConnection();
        verify(statement).executeUpdate(any());
    }

    @Test
    void deleteAll_dbUnavailable_failed() throws SQLException {
//        given
        doThrow(RuntimeException.class).when(datasource).getConnection();

//        when
        Executable executable = () -> ticketRepository.deleteAll();

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
        Executable executable = () -> ticketRepository.deleteAll();

//        then
        assertThrows(RuntimeException.class, executable);

        verify(datasource).getConnection();
        verify(statement).executeUpdate(any());
    }

    private Ticket getTicket(LocalDateTime dateTime) {
        return getTicket(null, dateTime);
    }

    private Ticket getTicket(Long id, LocalDateTime dateTime) {
        Ticket ticket = new Ticket();
        ticket.setId(id);

        ticket.setDepartureDate(dateTime);
        ticket.setArrivalDate(dateTime);
        ticket.setPurchaseDate(dateTime);

        ticket.setPassengerId(PASSENGER_ID);

        return ticket;
    }
}
