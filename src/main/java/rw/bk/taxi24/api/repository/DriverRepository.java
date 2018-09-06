package rw.bk.taxi24.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.bk.taxi24.api.domain.Driver;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;


/**
 * Spring Data  repository for the Driver entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Page<Driver> findByStatus(Pageable pageable, DriverStatus status);
}
