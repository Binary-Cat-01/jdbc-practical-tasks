package com.walking.jdbc.service;

import com.walking.jdbc.db.Transaction;
import com.walking.jdbc.db.TransactionProcessor;
import com.walking.jdbc.model.Flight;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.model.Ticket;
import com.walking.jdbc.repository.PassengerRepository;
import com.walking.jdbc.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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
    private TransactionProcessor transactionProcessor;

    @Captor
    private ArgumentCaptor<LocalDateTime> localDateTimeCaptor;

    @Test
    void purchase_success_with_exists_passenger() {
//        given:
        var existsPassenger = createPassenger(LocalDateTime.now());
        doReturn(true).when(passengerRepository).existsById(existsPassenger.getId());

        var flight = createFlight();
        var expectedTicket = createTicket(existsPassenger.getId(), flight.getId());
        doReturn(expectedTicket.getId()).when(ticketRepository).getNextId();

        doAnswer(TicketServiceTest::setPassengerLastPurchase)
                .when(passengerService).changeLastPurchase(
                        any(Passenger.class), any(LocalDateTime.class));

        doAnswer(TicketServiceTest::executeAllTransaction).when(transactionProcessor)
                                                          .executeTransactional(anyList());

        var inOrder = inOrder(passengerRepository, ticketRepository);

//        when:
        var actualTicket = ticketService.purchase(existsPassenger, flight);

//        then:
        assertEquals(expectedTicket.getId(), actualTicket.getId());
        assertEquals(expectedTicket.getPassengerId(), actualTicket.getPassengerId());
        assertEquals(expectedTicket.getFlightId(), actualTicket.getFlightId());

        /* Через assertEquals purchaseDate проверить не получится, т.к. она будет сгенерирована
         * в процессе выполнения и у нас нет доступа к объекту Ticket, т.к. он создается
         * внутри тестируемого метода.
         *
         * Вариант 1: Передавать время создания билета, как параметр метода purchase.
         * Тогда можно замокать его в тестовом методе.
         *
         * Вариант 2: Вынести логику определения времени создания билета в отдельный класс,
         * например PurchaseDateService. Тогда можно замокать его в тестовом методе.
         *
         * Вариант 3: Перехватить сгенерированное значение purchaseDate с помощью ArgumentCaptor
         * во время его передачи моку PassengerService. Затем сравнить его со значением у объекта
         * Ticket, который возвращается методом Purchase.
         *
         * Буду считать, что изменение сигнатуры метода purchase нежелательно - вариант №1 отпадает.
         * Воспользуюсь вариантом №3, чтобы попрактиковаться с ArgumentCaptor, хотя с точки зрения
         * декомпозиции вероятно стоило бы реализовать вариант №2*/
        verify(passengerService).changeLastPurchase(
                eq(existsPassenger), localDateTimeCaptor.capture());

        assertEquals(localDateTimeCaptor.getValue(), actualTicket.getPurchaseDate());
        assertEquals(existsPassenger.getLastPurchase(), actualTicket.getPurchaseDate());

        /*Ссылки на методы (и объекты функциональных интерфейсов, которые для них используются)
        * сравнить через assertEquals не получится (это кстати было интересное открытие =).
        * Поэтому, чтобы протестировать логику выбора нужного метод-референса в зависимости
        * от существования пассажира использую следующий подход. Мок transactionalProcessor
        * сконфигурирую так, чтобы он выполнял фактически переданные в него объекты
        * Transaction. А с помощью verify проверю, что запускались именно те метод-референсы,
        * которые ожидаются в данном тестовом сценарии. */
        inOrder.verify(passengerRepository).updateLastPurchaseTransactional(
                any(Connection.class), eq(existsPassenger));

        inOrder.verify(ticketRepository).createTransactional(
                any(Connection.class), eq(actualTicket));
    }

    @Test
    void purchase_success_with_not_exists_passenger() {
//        given:
        var notExistsPassenger = createPassenger(null);
        doReturn(false).when(passengerRepository).existsById(notExistsPassenger.getId());

        var flight = createFlight();
        var expectedTicket = createTicket(notExistsPassenger.getId(), flight.getId());
        doReturn(expectedTicket.getId()).when(ticketRepository).getNextId();

        doAnswer(TicketServiceTest::setPassengerLastPurchase)
                .when(passengerService).changeLastPurchase(
                        any(Passenger.class), any(LocalDateTime.class));

        doAnswer(TicketServiceTest::executeAllTransaction).when(transactionProcessor)
                                                          .executeTransactional(anyList());

        var inOrder = inOrder(passengerRepository, ticketRepository);

//        when:
        var actualTicket = ticketService.purchase(notExistsPassenger, flight);

//        then:
        assertEquals(expectedTicket.getId(), actualTicket.getId());
        assertEquals(expectedTicket.getPassengerId(), actualTicket.getPassengerId());
        assertEquals(expectedTicket.getFlightId(), actualTicket.getFlightId());

        verify(passengerService).changeLastPurchase(
                eq(notExistsPassenger), localDateTimeCaptor.capture());

        assertEquals(localDateTimeCaptor.getValue(), actualTicket.getPurchaseDate());
        assertEquals(notExistsPassenger.getLastPurchase(), actualTicket.getPurchaseDate());

        inOrder.verify(passengerRepository).createTransactional(
                any(Connection.class), eq(notExistsPassenger));

        inOrder.verify(ticketRepository).createTransactional(
                any(Connection.class), eq(actualTicket));
    }

    private Flight createFlight() {
        Flight flight = new Flight();

        flight.setId(1L);
        flight.setDepartureAirportId(1L);
        flight.setArrivalAirportId(2L);
        flight.setDepartureDate(
                LocalDateTime.of(2025, Month.APRIL, 1, 12, 0));
        flight.setArrivalDate(flight.getDepartureDate().plusHours(2));
        flight.setNumber("TEST");

        return  flight;
    }

    private Passenger createPassenger(LocalDateTime lastPurchase) {
        Passenger passenger = new Passenger();

        passenger.setId(1L);
        passenger.setFirstName("Jack");
        passenger.setLastName("Black");
        passenger.setBirthDate(LocalDate.of(1990, Month.JANUARY, 1));
        passenger.setMale(true);
        passenger.setLastPurchase(lastPurchase);

        return passenger;
    }

    private Ticket createTicket(Long passengerId, Long flightId) {
        Ticket ticket = new Ticket();

        ticket.setId(1L);
        ticket.setPassengerId(passengerId);
        ticket.setFlightId(flightId);
        ticket.setPurchaseDate(LocalDateTime.now());

        return ticket;
    }

    private static Object setPassengerLastPurchase(InvocationOnMock invocation) {
        LocalDateTime purchaseDate = invocation.getArgument(1);

        Passenger passenger = invocation.getArgument(0);

        passenger.setLastPurchase(purchaseDate);

        return passenger;
    }

    private static Object executeAllTransaction(InvocationOnMock invocation) {
        List<Transaction> transactionList = invocation.getArgument(0);

        Connection connectionMock = mock(Connection.class);

        transactionList.forEach(transaction -> transaction.getMethod()
                                                          .executeTransactional(connectionMock,
                                                                  transaction.getObject()));

        return null;
    }
}
