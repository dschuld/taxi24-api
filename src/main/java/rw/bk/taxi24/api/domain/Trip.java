package rw.bk.taxi24.api.domain;


import rw.bk.taxi24.api.domain.enumeration.TripStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Trip.
 */
@Entity
@Table(name = "trip")
public class Trip implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driverId", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "riderId", nullable = false)
    private Rider rider;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "trip_status", nullable = false)
    private TripStatus tripStatus;

    @Column(name = "duration")
    private Float duration;

    @Column(name = "distance")
    private Float distance;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public Trip driver(Driver driver) {
        this.driver = driver;
        return this;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Rider getRider() {
        return rider;
    }

    public Trip rider(Rider rider) {
        this.rider = rider;
        return this;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public Trip tripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
        return this;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }

    public Float getDuration() {
        return duration;
    }

    public Trip duration(Float duration) {
        this.duration = duration;
        return this;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public Float getDistance() {
        return distance;
    }

    public Trip distance(Float distance) {
        this.distance = distance;
        return this;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trip trip = (Trip) o;
        if (trip.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), trip.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Trip{" +
            "id=" + getId() +
            ", driverId=" + getDriver().getId() +
            ", riderId=" + getRider().getId() +
            ", tripStatus='" + getTripStatus() + "'" +
            ", duration=" + getDuration() +
            ", distance=" + getDistance() +
            "}";
    }
}
