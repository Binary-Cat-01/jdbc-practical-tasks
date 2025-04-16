package com.walking.jdbc.service;

import com.walking.jdbc.db.TransactionProcessor;
import com.walking.jdbc.db.Transaction;
import com.walking.jdbc.db.Transactional;
import com.walking.jdbc.model.Flight;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.model.Ticket;
import com.walking.jdbc.repository.PassengerRepository;
import com.walking.jdbc.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketService {
    private final PassengerService passengerService;
    private final TicketRepository ticketRepository;
    private final PassengerRepository passengerRepository;
    private final TransactionProcessor transactionProcessor;

    public TicketService(PassengerService passengerService, TicketRepository ticketRepository,
            PassengerRepository passengerRepository, TransactionProcessor transactionProcessor) {
        this.passengerService = passengerService;
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
        this.transactionProcessor = transactionProcessor;
    }

    /*Логика транзакционного выполнения запросов к бд не должна находится в сервисе
    * работы с билетами. Он должен содержать только логику по обработке полученных данных и
    * созданию запроса на транзакционное выполнение нужных методов из репозиториев. Так как для
    * выполнения транзакционного запроса нам будут нужны методы из разных репозиториев и один и тот
    * же объект Connection, мы не можем разместить эту функциональность в одном из репозиториев.
    * Эту функциональность можно разместить в классе-посреднике - TransactionProcessor.
    * Ему будем передавать список значений: объект для которого нужно выполнить метод репозитория и
    * код метода репозитория. Для упаковки кода метода репозитория создадим функциональный интерфейс
    * Transactional, а пары значений завернем в класс Transaction.
    * В классах репозиториях придется создать отдельные методы для
    * транзакционного взаимодействия с бд. От существующих методов взаимодействия с бд
    * они будут отличаться тем, что принимают объект Connection как параметр метода и в них нужно
    * явно кастовать Object в корректный для данного репозитория тип объекта (Ticket или Passenger).*/
    public Ticket purchase(Passenger passenger, Flight flight) {
        Ticket ticket = buildTicketForPurchase(passenger, flight);

        /*Если транзакция будет откачена, у объекта passenger в памяти станет некорректное
        * значение lastPurchase, т.к. мы меняем его до выполнения транзакции. Это может
        * быть проблемой, если этот же объект продолжает использоваться другими методами.
        * Но чтобы не усложнять буду считать, что он используется только в этом методе */
        passengerService.changeLastPurchase(passenger, ticket.getPurchaseDate());

        List<Transaction> transactions = getTransactionsForPurchase(passenger, ticket);

        transactionProcessor.executeTransactional(transactions);

        return ticket;
    }

    private Ticket buildTicketForPurchase(Passenger passenger, Flight flight) {
        Ticket ticket = new Ticket();

        ticket.setId(ticketRepository.getNextId());
        ticket.setPassengerId(passenger.getId());
        ticket.setFlightId(flight.getId());

        LocalDateTime purchaseDate = LocalDateTime.now();

        ticket.setPurchaseDate(purchaseDate);

        return ticket;
    }

    private List<Transaction> getTransactionsForPurchase(Passenger passenger, Ticket ticket) {
        List<Transaction> transactions = new ArrayList<>();

        boolean isExistsPassenger = passengerRepository.existsById(passenger.getId());

        transactions.add(
                new Transaction(passenger, getTransactionalExecutorFor(isExistsPassenger)));
        transactions.add(
                new Transaction(ticket, ticketRepository::createTransactional));

        return transactions;
    }

    private Transactional getTransactionalExecutorFor(boolean isExistsPassenger) {
        return isExistsPassenger
                ? passengerRepository::updateLastPurchaseTransactional
                : passengerRepository::createTransactional;
    }
}
