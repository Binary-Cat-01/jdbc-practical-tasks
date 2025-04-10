package com.walking.jdbc.service;

import com.walking.jdbc.db.TransactionProcessor;
import com.walking.jdbc.db.TransactionalData;
import com.walking.jdbc.db.TransactionalExecutor;
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
    работы с билетами. Он должен содержать только логику по обработке полученных данных и
    созданию запроса на транзакционное выполнение нужных методов из репозиториев. Так как для
    выполнения транзакционного запроса нам будут нужны методы из разных репозиториев и один и тот
    же объект Connection, мы не можем разместить эту функциональность в одном из репозиториев.
    Эту функциональность можно разместить в классе-посреднике - TransactionalProcessor.
    В него нужно передавать список из пар: сущность с которой нужно выполнить метод репозитория и
    код метода репозитория. Эти пары упакуем в класс transactionalData. Код конкретного метода
    репозитория будем передавать как ссылку на метод, используя созданный функциональный интерфейс
    TransactionalExecutor. Так же в классах репозиториях придется создать отдельные методы для
    транзакционного выполнения. Они будут отличаться тем, что принимают объект Connection
    как параметр метода, и в них нужно явно кастовать Object в нужную сущность (Ticket или Passenger).
    Момент с кастом из Object потенциально может привести к ClassCastException, если использовать
    объект TransactionalData, в котором тип фактически передаваемой сущности не совпадет с фактически
    переданным кодом метода репозитория. Устранить эту проблему с помощью параметризации у меня не
    получилось.*/
    public Ticket purchase(Passenger passenger, Flight flight) {
        Ticket ticket = buildTicketForPurchase(passenger, flight);

        passengerService.changeLastPurchase(passenger, ticket.getPurchaseDate());

        boolean existsPassenger = passengerRepository.existsById(passenger.getId());

        List<TransactionalData> transactionalData = new ArrayList<>();
        transactionalData.add(
                new TransactionalData(passenger, getTransactionalExecutorFor(existsPassenger)));
        transactionalData.add(
                new TransactionalData(ticket, ticketRepository::createTransactional));

        transactionProcessor.makeTransactional(transactionalData);

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

    private TransactionalExecutor getTransactionalExecutorFor(boolean existsPassenger) {
        return existsPassenger
                ? passengerRepository::updateLastPurchaseTransactional
                : passengerRepository::createTransactional;
    }
}
