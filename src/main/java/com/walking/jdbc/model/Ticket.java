package com.walking.jdbc.model;

import java.time.LocalDateTime;

public class Ticket {
    private Long id;
    private Long passengerId;
    private Long flightId;

    private LocalDateTime purchaseDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Ticket ticket = (Ticket) object;
        return id.equals(ticket.id) && passengerId.equals(ticket.passengerId) && flightId.equals(
                ticket.flightId) && purchaseDate.equals(ticket.purchaseDate);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + passengerId.hashCode();
        result = 31 * result + flightId.hashCode();
        result = 31 * result + purchaseDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Ticket{" + "id=" + id + ", passengerId=" + passengerId + ", flightId=" + flightId
                + ", purchaseDate=" + purchaseDate + '}';
    }
}
