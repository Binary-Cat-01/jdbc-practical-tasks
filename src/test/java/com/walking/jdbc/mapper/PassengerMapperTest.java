package com.walking.jdbc.mapper;

import com.walking.jdbc.model.Passenger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PassengerMapperTest {
    private static ResultSet resultSet;
    private PassengerMapper passengerMapper;

    @BeforeAll
    static void beforeAll() {
        resultSet = Mockito.mock(ResultSet.class);
    }

    @BeforeEach
    void setUp() {
        passengerMapper = new PassengerMapper();
    }

    @Test
    void map() throws SQLException {
//        given:
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.now();

        Passenger passenger = new Passenger();
        passenger.setId(1L);
        passenger.setFirstName("first_name");
        passenger.setLastName("last_name");
        passenger.setMale(true);
        passenger.setBirthDate(localDate);
        passenger.setLastPurchase(localDateTime);

        Mockito.doReturn(true,true, true, true, true, true, false).when(resultSet).next();

        Mockito.doReturn(1L).when(resultSet).getLong("id");
        Mockito.doReturn("first_name").when(resultSet).getString("first_name");
        Mockito.doReturn("last_name").when(resultSet).getString("last_name");
        Mockito.doReturn(true).when(resultSet).getBoolean("male");
        Mockito.doReturn(Date.valueOf(localDate)).when(resultSet).getDate("birth_date");
        Mockito.doReturn(Timestamp.valueOf(localDateTime)).when(resultSet).getTimestamp("last_purchase");
//        when:
        List<Passenger> passengerList = passengerMapper.map(resultSet);
//        then:
        assertEquals(passenger, passengerList.get(0));
    }
}