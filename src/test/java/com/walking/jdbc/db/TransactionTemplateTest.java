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
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionTemplateTest {
    @Mock
    private DataSource datasource;

    @InjectMocks
    private TransactionTemplate transactionTemplate;

    @Test
    void runTransactional_commit_success() throws SQLException {
        Connection connection = mock(Connection.class);
        doReturn(connection).when(datasource).getConnection();
        Function<Connection, Object> function = Object.class::cast; // Гарантированно безопасное выражение

        Executable executable = () -> transactionTemplate.runTransactional(function);

        assertDoesNotThrow(executable);
        verify(connection).commit();
    }

    @Test
    void runTransactional_dbUnavailable_failed() throws SQLException {
        doThrow(RuntimeException.class).when(datasource).getConnection();
        Function<Connection, Object> function = Object.class::cast; // Гарантированно безопасное выражение

        Executable executable = () -> transactionTemplate.runTransactional(function);

        assertThrows(RuntimeException.class, executable);
    }

    @Test
    void runTransactional_rollback_failed() throws SQLException {
//        given
        Connection connection = mock(Connection.class);
        doReturn(connection).when(datasource).getConnection();

        Function<Connection, Object> function = c -> {
            throw new RuntimeException();
        };

//        when
        Executable executable = () -> transactionTemplate.runTransactional(function);

//        then
        assertThrows(RuntimeException.class, executable);
        verify(connection).rollback();
    }
}
