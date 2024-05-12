package com.walking.jdbc.service.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class PassengerMigrationServiceTest {
    @Mock
    private DataSource datasource;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @InjectMocks
    private PassengerMigrationService passengerMigrationService;

    @Test
    void migrate_success() throws SQLException {
        doReturn(connection).when(datasource).getConnection();
        doReturn(statement).when(connection).createStatement();

        Executable executable = passengerMigrationService::migrate;

        assertDoesNotThrow(executable);
    }

    @Test
    void migrate_dbUnavailable_failed() throws SQLException {
        doThrow(RuntimeException.class).when(datasource).getConnection();

        Executable executable = passengerMigrationService::migrate;

        assertThrows(RuntimeException.class, executable);
    }

    @Test
    void migrate_executeError_failed() throws SQLException {
        doReturn(connection).when(datasource).getConnection();
        doReturn(statement).when(connection).createStatement();
        doThrow(SQLException.class).when(statement).executeUpdate(any());

        Executable executable = passengerMigrationService::migrate;

        assertThrows(RuntimeException.class, executable);
    }
}
