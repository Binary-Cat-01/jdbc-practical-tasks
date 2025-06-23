package com.walking.jdbc.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public class TransactionExecutor {
    private final DataSource dataSource;

    public TransactionExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(List<TransactionQuery<?>> transactionQueries) {
        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            /*Второй блок try-catch тут нужен только, чтобы был доступ к переменной connection,
             * чтобы в catch вызвать у нее rollback(), потому что в первом блоке catch вызвать
             * rollback нельзя? */
            try {
                for (TransactionQuery<?> query : transactionQueries) {
                    query.executeOn(connection);
                }

                connection.commit();

                /*Если у нас есть какая-то дополнительная логика в try-блоке, выполнение которой
                 * может вызвать исключение, мы можем отлавливать его в отдельном catch-блоке
                 * и в этом же отдельном блоке тоже вызывать connection.rollback().
                 * При этом у нас также должен остаться catch-блок перехватывающий SQLException
                 * т.к. мы обязаны его обработать. То есть у нас будет несколько catch-блоков
                 * перехватывающих разные типы исключений, но в каждом должен быть
                 * вызван connection.rollback(). Тогда наверное можно вынести connection.rollback()
                 * в finally-блок. Либо мы используем единственный catch-блок
                 * с более общим типом исключения, который сможет поймать нужное нам исключение
                 * + SQLException. И в этом единственном блоке размещаем логику зависящую от типа
                 * перехваченного исключения (if или switch) + вызываем connection.rollback(). */
            } catch (Exception e) {
                connection.rollback();

                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Ошибка при выполнении транзакции '%s'. Транзакция была откачена"
                            .formatted(transactionQueries), e);
        }
    }
}
