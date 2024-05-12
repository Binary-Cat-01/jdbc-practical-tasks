package com.walking.jdbc;

import com.walking.jdbc.service.migration.MigrationService;
import com.walking.jdbc.service.migration.PassengerMigrationService;
import com.walking.jdbc.service.migration.TicketMigrationService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.List;

/**
 * Задача урока 129:
 * <a href="https://github.com/KFalcon2022/lessons/blob/master/lessons/jdbc/129/Statement.%20DDL.%20ResultSet.md#%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B0">ссылка</a>
 * <p>
 * Задача урока 130:
 * <a href="https://github.com/KFalcon2022/lessons/blob/master/lessons/jdbc/130/PreparedStatement.%20SQL%20injection.md#%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B0">ссылка</a>
 * <p>
 * Задача урока 131:
 * <a href="https://github.com/KFalcon2022/lessons/blob/master/lessons/jdbc/131/Batch.md#%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B0">ссылка</a>
 * <p>
 * Задача урока 132:
 * <a href="https://github.com/KFalcon2022/lessons/blob/master/lessons/jdbc/132/JDBC.%20Tranastions.md#%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B0">ссылка</a>
 */
public class Main {
    public static void main(String[] args) {
        var configuration = new HikariConfig("./src/main/resources/hikari.properties");

        var datasource = new HikariDataSource(configuration);

        List.of(new PassengerMigrationService(datasource), new TicketMigrationService(datasource))
                .forEach(MigrationService::migrate);
    }
}
