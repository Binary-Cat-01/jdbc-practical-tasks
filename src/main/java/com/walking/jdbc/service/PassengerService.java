package com.walking.jdbc.service;

import com.walking.jdbc.model.Passenger;

import java.time.LocalDateTime;

public class PassengerService {
    public PassengerService() {
    }

    public Passenger changeLastPurchase(Passenger passenger, LocalDateTime time) {
        passenger.setLastPurchase(time);

        return passenger;
    }
}
