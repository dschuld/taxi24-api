package rw.bk.taxi24.api.domain;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Rider entity.
 */
@ApiModel(description = "The Rider entity.")
@Entity
@Table(name = "rider")
public class Rider implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "amount_rides")
    private Integer amountRides;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Rider name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmountRides() {
        return amountRides;
    }

    public Rider amountRides(Integer amountRides) {
        this.amountRides = amountRides;
        return this;
    }

    public void setAmountRides(Integer amountRides) {
        this.amountRides = amountRides;
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
        Rider rider = (Rider) o;
        if (rider.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), rider.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Rider{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", amountRides=" + getAmountRides() +
            "}";
    }
}
