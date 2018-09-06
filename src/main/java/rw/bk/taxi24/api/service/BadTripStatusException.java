package rw.bk.taxi24.api.service;

import rw.bk.taxi24.api.domain.enumeration.TripStatus;

public class BadTripStatusException extends RuntimeException {

    public BadTripStatusException(long tripId, TripStatus status) {
        super("Trip with ID " + tripId + " has status " + status);
    }
}
