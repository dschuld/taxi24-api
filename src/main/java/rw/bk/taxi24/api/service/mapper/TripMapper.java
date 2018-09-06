package rw.bk.taxi24.api.service.mapper;

import rw.bk.taxi24.api.domain.*;
import rw.bk.taxi24.api.service.dto.TripDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Trip and its DTO TripDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TripMapper extends EntityMapper<TripDTO, Trip> {



    default Trip fromId(Long id) {
        if (id == null) {
            return null;
        }
        Trip trip = new Trip();
        trip.setId(id);
        return trip;
    }
}
