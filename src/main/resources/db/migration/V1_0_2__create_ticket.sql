create table if not exists ticket (
    id                  bigserial       primary key,
    purchase_date       timestamp       not null,
    passenger_id        bigint          not null references passenger(id),
    flight_id           bigint          not null references flight(id),

    constraint ticket_passenger_id_flight_id_key unique
        (passenger_id, flight_id)
);