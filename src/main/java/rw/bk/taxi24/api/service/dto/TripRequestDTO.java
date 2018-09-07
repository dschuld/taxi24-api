package rw.bk.taxi24.api.service.dto;

public class TripRequestDTO {

    private long driverId;

    private long riderId;

    public TripRequestDTO() {
        //empty default
    }

    public TripRequestDTO(Long driverId, Long riderId) {
        this.driverId = driverId;
        this.riderId = riderId;
    }

    public long getRiderId() {
        return riderId;
    }

    public void setRiderId(long riderId) {
        this.riderId = riderId;
    }


    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }
}
