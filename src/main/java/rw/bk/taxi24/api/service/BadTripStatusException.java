package rw.bk.taxi24.api.service;

import rw.bk.taxi24.api.domain.enumeration.TripStatus;

/**
 * Is thrown when a trip shall be transitioned to a new state that is not valid for the old state.
 */
public class BadTripStatusException extends RuntimeException {

    public BadTripStatusException(long tripId, TripStatus status) {
        super("Trip with ID " + tripId + " has status " + status);
    }
}
