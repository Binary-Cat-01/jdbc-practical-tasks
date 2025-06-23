package com.walking.jdbc.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionExecutorTest {
    @InjectMocks
    private TransactionExecutor transactionExecutor;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private TransactionQuery<?> query1, query2;

    @Test
    void execute_shouldCommit_whenSuccess() throws SQLException {
//        given:
        doReturn(connection).when(dataSource).getConnection();

        var transactionQueries = List.of(query1, query2);

//        when:
        transactionExecutor.execute(transactionQueries);

//        then:
        verify(connection).setAutoCommit(false);
        verify(query1).executeOn(connection);
        verify(query2).executeOn(connection);
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).close();
    }

    @Test
    void execute_shouldRollback_whenFailed() throws SQLException {
//        given:
        doReturn(connection).when(dataSource).getConnection();

        RuntimeException testException = new RuntimeException();

        doThrow(testException).when(query2).executeOn(connection);

        var transactionQueries = List.of(query1, query2);

//        when:
        Executable actual = () -> transactionExecutor.execute(transactionQueries);

//        then:
        RuntimeException thrown = assertThrows(RuntimeException.class, actual);

        assertSame(testException, thrown.getCause());

        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).close();
    }
}
