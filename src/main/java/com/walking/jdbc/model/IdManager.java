package com.walking.jdbc.model;

import javax.sql.DataSource;
import java.sql.*;

public class IdManager {
    private final DataSource dataSource;

    public IdManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> Long getNextId(T entity) {
        String entityName = entity.getClass().getSimpleName().toLowerCase();

        String sql = "select nextval('%s_id_seq') as nextId".formatted(entityName);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet result = statement.executeQuery(sql);

            return result.getLong("nextId");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить значение id для '%s'"
                    .formatted(entity), e);
        }
    }
}
