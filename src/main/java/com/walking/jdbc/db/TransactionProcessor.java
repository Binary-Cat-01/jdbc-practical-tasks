package com.walking.jdbc.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionProcessor {
    private final DataSource dataSource;

    public TransactionProcessor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void makeTransactional(List<TransactionalData> transactionalData) {
        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            for (TransactionalData transaction : transactionalData) {
                var objectForTransaction = transaction.getObjectForTransaction();
                var methodForTransaction = transaction.getMethodForTransaction();

                methodForTransaction.executeTransactional(connection, objectForTransaction);
            }

            try {
                connection.commit();
            } catch (Exception e) {
                connection.rollback();

                /*Если здесь мы перехватили исключение, произошедшее во время транзакции и выполнили
                * роллбэк, должны ли мы пробросить это исключение (или новое исключение SQLException),
                * которое будет перехвачено следующим блоком catch? Кажется если этого не сделать,
                * метод вызывавший makeTransactional будет считать, что транзакция выполнена успешно.*/
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при попытке транзакционного выполнения. Транзакция была откачена", e);
        }
    }
}
