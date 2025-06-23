package com.walking.jdbc.service;

import com.walking.jdbc.db.TransactionExecutor;
import com.walking.jdbc.db.TransactionQuery;
import com.walking.jdbc.db.Query;
import com.walking.jdbc.model.Flight;
import com.walking.jdbc.model.IdManager;
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
    private final TransactionExecutor transactionExecutor;
    private final IdManager idManager;

    public TicketService(PassengerService passengerService, TicketRepository ticketRepository,
            PassengerRepository passengerRepository, TransactionExecutor transactionExecutor,
            IdManager idManager) {
        this.passengerService = passengerService;
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
        this.transactionExecutor = transactionExecutor;
        this.idManager = idManager;
    }

    /**Логика транзакционного выполнения запросов к бд не должна находится в сервисе
     * работы с билетами. Он должен содержать только логику по обработке полученных данных и
     * созданию запроса на транзакционное выполнение нужных методов из репозиториев. Так как для
     * выполнения транзакционного запроса нам будут нужны методы из разных репозиториев и один и тот
     * же объект Connection, мы не можем разместить эту функциональность в одном из репозиториев.
     * Эту функциональность можно разместить в классе-посреднике - TransactionExecutor.
     * Ему будем передавать список значений: объект для которого нужно выполнить метод репозитория и
     * код метода репозитория. Для упаковки кода метода репозитория создадим функциональный интерфейс
     * Query, а пары значений завернем в класс TransactionQuery.
     * В классах репозиториях придется создать отдельные методы для
     * транзакционного взаимодействия с бд. От существующих методов взаимодействия с бд
     * они будут отличаться тем, что принимают объект Connection как параметр метода.
     **/
    public Ticket purchase(Passenger passenger, Flight flight) {
        Ticket ticket = buildTicketForPurchase(passenger, flight);

        /*Если транзакция будет откачена, у объекта passenger в памяти станет некорректное
         * значение lastPurchase, т.к. мы меняем его до выполнения транзакции. Это может
         * быть проблемой, если этот же объект продолжает использоваться другими методами.
         * Но чтобы не усложнять буду считать, что он используется только в этом методе */
        passengerService.changeLastPurchase(passenger, ticket.getPurchaseDate());

        transactionExecutor.execute(getTransactionQueriesForPurchase(passenger, ticket));

        return ticket;
    }

    private Ticket buildTicketForPurchase(Passenger passenger, Flight flight) {
        Ticket ticket = new Ticket();

        ticket.setId(idManager.getNextId(ticket));
        ticket.setPassengerId(passenger.getId());
        ticket.setFlightId(flight.getId());

        LocalDateTime purchaseDate = LocalDateTime.now();

        ticket.setPurchaseDate(purchaseDate);

        return ticket;
    }

    private List<TransactionQuery<?>> getTransactionQueriesForPurchase(Passenger passenger,
            Ticket ticket) {

        List<TransactionQuery<?>> transactionQueries = new ArrayList<>();

        transactionQueries.add(new TransactionQuery<>(passenger, passengerRepository::createOrUpdate));
        transactionQueries.add(new TransactionQuery<>(ticket, ticketRepository::create));

        return transactionQueries;
    }
}
