package com.walking.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;

public class Main {
    private final static Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        var config = new HikariConfig("./src/main/resources/hikari.properties");
        var dataSource = new HikariDataSource(config);

        Flyway.configure()
              .dataSource(dataSource)
              .baselineOnMigrate(true)
              .load()
              .migrate();
    }
}
