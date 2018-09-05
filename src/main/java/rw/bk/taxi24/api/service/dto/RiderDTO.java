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
            "}";
    }
}
