package rw.bk.taxi24.api.service;

public class DriverNotAvailableException extends RuntimeException{


    public DriverNotAvailableException(Long driverId) {
        super("Driver with ID " + driverId + " is not available");
    }
}
