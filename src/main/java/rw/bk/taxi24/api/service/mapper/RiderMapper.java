package rw.bk.taxi24.api.service.mapper;

import rw.bk.taxi24.api.domain.*;
import rw.bk.taxi24.api.service.dto.RiderDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Rider and its DTO RiderDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RiderMapper extends EntityMapper<RiderDTO, Rider> {



    default Rider fromId(Long id) {
        if (id == null) {
            return null;
        }
        Rider rider = new Rider();
        rider.setId(id);
        return rider;
    }
}
