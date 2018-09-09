package rw.bk.taxi24.api.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.domain.enumeration.TripStatus;

/**
 * A DTO for the Trip entity.
 */
public class TripDTO implements Serializable {

    private Long id;

    private DriverDTO driver;

    private RiderDTO rider;

    private TripStatus tripStatus;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    }

    public RiderDTO getRider() {
        return rider;
    }

    public void setRider(RiderDTO rider) {
        this.rider = rider;
    }

    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TripDTO tripDTO = (TripDTO) o;
        if (tripDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tripDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TripDTO{" +
            "id=" + getId() +
            ", driverId=" + getDriver().getId() +
            ", riderId=" + getRider().getId() +
            ", tripStatus='" + getTripStatus() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
