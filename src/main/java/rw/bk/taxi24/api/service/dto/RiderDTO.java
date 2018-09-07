package rw.bk.taxi24.api.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Rider entity.
 */
public class RiderDTO implements Serializable {

    private Long id;

    private String name;

    private Integer amountRides;

    private Double latitude;


    private Double longitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmountRides() {
        return amountRides;
    }

    public void setAmountRides(Integer amountRides) {
        this.amountRides = amountRides;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RiderDTO riderDTO = (RiderDTO) o;
        if (riderDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), riderDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RiderDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", amountRides=" + getAmountRides() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            "}";
    }
}
