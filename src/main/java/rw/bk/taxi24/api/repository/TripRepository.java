package rw.bk.taxi24.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.bk.taxi24.api.domain.Trip;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import rw.bk.taxi24.api.domain.enumeration.TripStatus;



/**
 * Spring Data  repository for the Trip entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {


    Page<Trip> findByTripStatus(Pageable pageable, TripStatus tripStatus);

}
