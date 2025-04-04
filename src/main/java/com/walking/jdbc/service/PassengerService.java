package com.walking.jdbc.service;

import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.repository.PassengerRepository;

import java.time.LocalDateTime;

public class PassengerService {
    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public Passenger changeLastPurchase(Passenger passenger, LocalDateTime time) {
        passenger.setLastPurchase(time);

        return passenger;
    }
}
