package com.walking.jdbc.mapper;

import com.walking.jdbc.model.Passenger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PassengerMapperTest {
    private PassengerMapper passengerMapper;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        passengerMapper = new PassengerMapper();
    }

    @Test
    void map() throws SQLException {
//        given:
        Passenger testPassenger = getTestPassenger();

        Mockito.doReturn(true,true, true, true, true, true, false).when(resultSet).next();

        Mockito.doReturn(testPassenger.getId()).when(resultSet).getLong("id");
        Mockito.doReturn(testPassenger.getFirstName()).when(resultSet).getString("first_name");
        Mockito.doReturn(testPassenger.getLastName()).when(resultSet).getString("last_name");
        Mockito.doReturn(testPassenger.isMale()).when(resultSet).getBoolean("male");
        Mockito.doReturn(Date.valueOf(testPassenger.getBirthDate())).when(resultSet).getDate("birth_date");
        Mockito.doReturn(Timestamp.valueOf(testPassenger.getLastPurchase())).when(resultSet).getTimestamp("last_purchase");
//        when:
        List<Passenger> passengerList = passengerMapper.map(resultSet);
//        then:
        assertEquals(testPassenger, passengerList.get(0));
    }

    private Passenger getTestPassenger() {
        Passenger passenger = new Passenger();

        passenger.setId(1L);
        passenger.setFirstName("first_name");
        passenger.setLastName("last_name");
        passenger.setMale(true);
        passenger.setBirthDate(LocalDate.now());
        passenger.setLastPurchase(LocalDateTime.now());

        return passenger;
    }
}