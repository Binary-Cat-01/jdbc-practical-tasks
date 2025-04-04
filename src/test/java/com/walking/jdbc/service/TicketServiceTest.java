package com.walking.jdbc.service;

import com.walking.jdbc.model.Flight;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.model.Ticket;
import com.walking.jdbc.repository.PassengerRepository;
import com.walking.jdbc.repository.TicketRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @InjectMocks
    TicketService ticketService;

    @Mock
    TicketRepository ticketRepository;

    @Mock
    PassengerRepository passengerRepository;

    @Mock
    DataSource dataSource;

    @Test
    void buy_success_with_exists_passenger() throws SQLException {
//        given:
        Connection connection = mock(Connection.class);
        doReturn(connection).when(dataSource).getConnection();

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        doReturn(getExpectedTicket().getId()).when(ticketRepository).getNextId();

        doReturn(true).when(passengerRepository).existsById(any());

//        when:
        Ticket actualTicket = ticketService.buy(getExpectedExistsPassenger(), getExpectedFlight());

//        then:
        assertEquals(getExpectedTicket().getId(), actualTicket.getId());
        assertEquals(getExpectedTicket().getPassengerId(), actualTicket.getPassengerId());
        assertEquals(getExpectedTicket().getFlightId(), actualTicket.getFlightId());

        /* Через assertEquals purchaseDate проверить не получится, т.к. она будет сгенерирована
        * в процессе выполнения и у нас нет доступа к объекту Ticket, т.к. он создается
        * внутри тестируемого метода.
        * Вариант 1: Если передавать время создания билета, как параметр метода purchase,
        * можно замокать его в тестовом методе.
        * Вариант 2: Перехватить сгенерированное значение purchaseDate, с помощью ArgumentCaptor
        * во время его передачи моку PassengerService. Затем сравнить его со значением у объекта
        * Ticket, который возвращается методом Purchase.
        * Буду считать, что изменение сигнатуры метода purchase нежелательно и
        * воспользуюсь вариантом №2.*/

    }

    @Test
    void buy_success_with_not_exists_passenger() {
//        given:


//        when:


//        then:

    }

    private Passenger getExpectedNotExistsPassenger() {
        Passenger passenger = new Passenger();

        passenger.setId(1L);
        passenger.setFirstName("Jack");
        passenger.setLastName("Black");
        passenger.setBirthDate(LocalDate.now());
        passenger.setMale(true);
        passenger.setLastPurchase(null);

        return passenger;
    }

    private Passenger getExpectedExistsPassenger() {
        Passenger passenger = new Passenger();

        passenger.setId(1L);
        passenger.setFirstName("Jack");
        passenger.setLastName("Black");
        passenger.setBirthDate(LocalDate.now());
        passenger.setMale(true);
        passenger.setLastPurchase(LocalDateTime.now());

        return passenger;
    }

    private Flight getExpectedFlight() {
        Flight flight = new Flight();

        flight.setId(1L);
        flight.setDepartureAirportId(1L);
        flight.setArrivalAirportId(2L);
        flight.setDepartureDate(LocalDateTime.now());
        flight.setArrivalDate(LocalDateTime.now().plusHours(2));
        flight.setNumber("TEST");
        
        return  flight;
    }

    private Ticket getExpectedTicket() {
        Ticket ticket = new Ticket();

        ticket.setId(1L);
        ticket.setPassengerId(getExpectedExistsPassenger().getId());
        ticket.setFlightId(getExpectedFlight().getId());

        LocalDateTime purchaseTime = LocalDateTime.now();
        ticket.setPurchaseDate(purchaseTime);

        return ticket;
    }
}
