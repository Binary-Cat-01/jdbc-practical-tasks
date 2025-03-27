package com.walking.jdbc.mapper;

import com.walking.jdbc.model.Passenger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerMapperTest {
    @InjectMocks
    private PassengerMapper passengerMapper;

    @Test
    void map_success() throws SQLException {
//        given:
        ResultSet resultSet = mock(ResultSet.class);
        doReturn(true,true, false).when(resultSet).next();

        Passenger expected1 = getPassengerWithoutNullField();
        Passenger expected2 = getPassengerWithNullLastPurchase();

        doReturn(expected1.getId(), expected2.getId())
                .when(resultSet).getLong("id");

        doReturn(expected1.getFirstName(), expected2.getFirstName())
                .when(resultSet).getString("first_name");

        doReturn(expected1.getLastName(), expected2.getLastName())
                .when(resultSet).getString("last_name");

        doReturn(expected1.isMale(), expected2.isMale())
                .when(resultSet).getBoolean("male");

        doReturn(Date.valueOf(expected1.getBirthDate()), Date.valueOf(expected2.getBirthDate()))
                .when(resultSet).getDate("birth_date");

        doReturn(Timestamp.valueOf(expected1.getLastPurchase()), (Object) null)
                .when(resultSet).getTimestamp("last_purchase");

        List<Passenger> expectedPassengers = List.of(expected1, expected2);

//        when:
        List<Passenger> result = passengerMapper.map(resultSet);

//        then:
        /*эти проверки кажутся избыточными, но я часто вижу, что так делают
        assertNotNull(result);
        assertEquals(expectedPassengers.size(), result.size());*/

        assertIterableEquals(expectedPassengers, result);

        verify(resultSet, times(3)).next();

        verify(resultSet, times(2)).getLong("id");
        verify(resultSet, times(2)).getString("first_name");
        verify(resultSet, times(2)).getString("last_name");
        verify(resultSet, times(2)).getBoolean("male");
        verify(resultSet, times(2)).getDate("birth_date");
        verify(resultSet, times(2)).getTimestamp("last_purchase");
    }

    @Test
    void map_success_with_empty_resultSet() throws SQLException {
//        given:
        ResultSet resultSet = mock(ResultSet.class);
        doReturn(false).when(resultSet).next();

//        when:
        List<Passenger> result = passengerMapper.map(resultSet);

//        then:
        assertTrue(result.isEmpty());

        verify(resultSet).next();

        verify(resultSet, never()).getLong("id");
        verify(resultSet, never()).getString("first_name");
        verify(resultSet, never()).getString("last_name");
        verify(resultSet, never()).getBoolean("male");
        verify(resultSet, never()).getDate("birth_date");
        verify(resultSet, never()).getTimestamp("last_purchase");
    }

    @Test
    void map_failed_cause_resultSet_not_contain_require_field() throws SQLException {
//        given:
        ResultSet resultSet = mock(ResultSet.class);
        doReturn(true).when(resultSet).next();

        doThrow(SQLException.class).when(resultSet).getLong(anyString());

//        when:
        Executable actual = () -> passengerMapper.map(resultSet);

//        then:
        assertThrows(SQLException.class, actual);

        verify(resultSet).next();
        verify(resultSet).getLong("id");

        verify(resultSet, never()).getString("first_name");
        verify(resultSet, never()).getString("last_name");
        verify(resultSet, never()).getBoolean("male");
        verify(resultSet, never()).getDate("birth_date");
        verify(resultSet, never()).getTimestamp("last_purchase");
    }

    Passenger getPassengerWithoutNullField() {
        Passenger passenger = new Passenger();

        passenger.setId(1L);
        passenger.setFirstName("first_name");
        passenger.setLastName("last_name");
        passenger.setBirthDate(LocalDate.now());
        passenger.setMale(true);
        passenger.setLastPurchase(LocalDateTime.now());

        return passenger;
    }

    Passenger getPassengerWithNullLastPurchase() {
        Passenger passenger = new Passenger();

        passenger.setId(2L);
        passenger.setFirstName("another_first_name");
        passenger.setLastName("another_last_name");
        passenger.setBirthDate(LocalDate.now());
        passenger.setMale(false);
        passenger.setLastPurchase(null);

        return passenger;
    }
}
