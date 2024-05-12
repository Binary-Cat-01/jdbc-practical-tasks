package com.walking.jdbc.service.migration;

import javax.sql.DataSource;
import java.sql.SQLException;

public class TicketMigrationService implements MigrationService {
    private final DataSource datasource;

    public TicketMigrationService(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public void migrate() {
        var sql = """
                 create table if not exists ticket (
                	id 					bigserial 		primary key,
                	departure_airport 	varchar(100) 	not null,
                	arrival_airport 	varchar(100) 	not null,
                	departure_date 		timestamp 		not null,
                	arrival_date 		timestamp 		not null,
                	purchase_date 		timestamp 		not null,
                	passenger_id 		bigint 			not null references passenger(id),
                	
                	constraint ticket_departure_airport_arrival_airport_departure_date_arr_key unique
                	    (departure_airport, arrival_airport, departure_date, arrival_date, passenger_id)
                );
                """;

        try (var connection = datasource.getConnection();
             var statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при применении миграции", e);
        }
    }
}
