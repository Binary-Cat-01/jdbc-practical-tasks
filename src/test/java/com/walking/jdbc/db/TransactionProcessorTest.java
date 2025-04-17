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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionProcessorTest {
    @InjectMocks
    TransactionProcessor transactionProcessor;

    @Mock
    DataSource dataSource;

    @Test
    void executeTransactional_success() throws SQLException {
//        given:
        var firstObject = new Object();
        var secondObject = new Object();

        var firstMethod = mock(Transactional.class);
        var secondMethod = mock(Transactional.class);

        var firstTransaction = createTransaction(firstObject, firstMethod);
        var secondTransaction = createTransaction(secondObject, secondMethod);

        var expectedTransactions = List.of(firstTransaction, secondTransaction);

        var connection = mock(Connection.class);
        doReturn(connection).when(dataSource).getConnection();

        var inOrder = inOrder(connection, firstMethod, secondMethod);

//        when:
        transactionProcessor.executeTransactional(expectedTransactions);

//        then:
        inOrder.verify(connection).setAutoCommit(false);
        inOrder.verify(firstMethod).executeTransactional(connection, firstObject);
        inOrder.verify(secondMethod).executeTransactional(connection, secondObject);
        inOrder.verify(connection).commit();

        verify(connection, never()).rollback();

        /*Или здесь уместнее более строгое verifyNoMoreInteractions(connection), т.к.
        * не только метод rollback() может "сломать" транзакцию? И чтобы это работало,
        * еще дополнительная проверка на вызов close()*/
        inOrder.verify(connection).close();
        verifyNoMoreInteractions(connection);
    }

    @Test
    void executeTransactional_make_rollback_when_commit_failed() throws SQLException {
//        given:
        var firstObject = new Object();
        var secondObject = new Object();

        var firstMethod = mock(Transactional.class);
        var secondMethod = mock(Transactional.class);

        var firstTransaction = createTransaction(firstObject, firstMethod);
        var secondTransaction = createTransaction(secondObject, secondMethod);

        var expectedTransactions = List.of(firstTransaction, secondTransaction);

        var connection = mock(Connection.class);
        doReturn(connection).when(dataSource).getConnection();

        doThrow(SQLException.class).when(connection).commit();

        var inOrder = inOrder(connection, firstMethod, secondMethod);

//        when:
        Executable actual = () -> transactionProcessor.executeTransactional(expectedTransactions);

//        then:
        assertThrows(RuntimeException.class, actual);

        inOrder.verify(connection).setAutoCommit(false);
        inOrder.verify(firstMethod).executeTransactional(connection, firstObject);
        inOrder.verify(secondMethod).executeTransactional(connection, secondObject);
        inOrder.verify(connection).commit();
        inOrder.verify(connection).rollback();
    }

    private Transaction createTransaction(Object object, Transactional method) {
        return new Transaction(object, method);
    }
}