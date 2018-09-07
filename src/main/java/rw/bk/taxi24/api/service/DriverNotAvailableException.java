package rw.bk.taxi24.api.service;

/**
 * Is thrown when a trip is requested for a driver that is not available.
 */
public class DriverNotAvailableException extends RuntimeException{


    public DriverNotAvailableException(Long driverId) {
        super("Driver with ID " + driverId + " is not available");
    }
}
