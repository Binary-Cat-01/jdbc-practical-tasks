package com.walking.jdbc.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Passenger {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private boolean male;
    private LocalDateTime lastPurchase;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public LocalDateTime getLastPurchase() {
        return lastPurchase;
    }

    public void setLastPurchase(LocalDateTime lastPurchase) {
        this.lastPurchase = lastPurchase;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Passenger passenger = (Passenger) object;
        return male == passenger.male && id.equals(passenger.id) && firstName.equals(
                passenger.firstName) && lastName.equals(passenger.lastName) && birthDate.equals(
                passenger.birthDate) && Objects.equals(lastPurchase, passenger.lastPurchase);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + birthDate.hashCode();
        result = 31 * result + Boolean.hashCode(male);
        result = 31 * result + Objects.hashCode(lastPurchase);
        return result;
    }

    @Override
    public String toString() {
        return "Passenger{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='"
                + lastName + '\'' + ", birthDate=" + birthDate + ", male=" + male
                + ", lastPurchase=" + lastPurchase + '}';
    }
}
