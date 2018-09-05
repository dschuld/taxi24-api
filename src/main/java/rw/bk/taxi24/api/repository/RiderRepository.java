package rw.bk.taxi24.api.repository;

import rw.bk.taxi24.api.domain.Rider;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Rider entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {

}
