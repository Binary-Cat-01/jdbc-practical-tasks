package com.walking.jdbc.service;

import com.walking.jdbc.db.TransactionQuery;
import com.walking.jdbc.db.TransactionExecutor;
import com.walking.jdbc.model.Flight;
import com.walking.jdbc.model.IdManager;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.repository.PassengerRepository;
import com.walking.jdbc.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @InjectMocks
    private TicketService ticketService;

    @Mock
    private PassengerService passengerService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private TransactionExecutor transactionExecutor;

    @Mock
    private IdManager idManager;

    @Mock
    private Connection connection;

    @Test
    void purchase_shouldCreateTicketAndUpdatePassengerLastPurchase_whenSuccess() {
//        given:
        var expectedPassenger = createPassenger();
        var expectedFlight = createFlight();

        doReturn(1L).when(idManager).getNextId(any());

        doAnswer(invocationOnMock -> {
            expectedPassenger.setLastPurchase(invocationOnMock.getArgument(1));

            return expectedPassenger;
        }).when(passengerService)
          .changeLastPurchase(any(), any());

        doAnswer(this::executeTransactionQueries).when(transactionExecutor).execute(anyList());

//        when:
        var actualTicket = ticketService.purchase(expectedPassenger, expectedFlight);

//        then:
        assertEquals(actualTicket.getId(), 1L);
        assertEquals(actualTicket.getPassengerId(), expectedPassenger.getId());
        assertEquals(actualTicket.getFlightId(), expectedFlight.getId());
        assertEquals(actualTicket.getPurchaseDate(), expectedPassenger.getLastPurchase());

        /*Так и не нашел способа сравнить две ссылки на методы, через assertEquals.
         * Поэтому подход остался прежний, сравниваю их через сравнение побочных эффектов.
         * В данном случае побочными эффектами являются вызовы определенных методов
         * репозиториев с заданными параметрами. Эти вызовы проверяем с помощью verify.*/
        verify(passengerRepository).createOrUpdate(connection, expectedPassenger);
        verify(ticketRepository).create(connection, actualTicket);
    }

    private Flight createFlight() {
        Flight flight = new Flight();

        flight.setId(1L);

        return flight;
    }

    private Passenger createPassenger() {
        Passenger passenger = new Passenger();

        passenger.setId(1L);

        return passenger;
    }

    private Object executeTransactionQueries(InvocationOnMock invocation) {
        List<TransactionQuery<?>> list = invocation.getArgument(0);

        for (var transactionQuery : list) {
            transactionQuery.executeOn(connection);
        }

        return null;
    }
}
