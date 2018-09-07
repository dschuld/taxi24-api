package rw.bk.taxi24.api.service;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.service.dto.DriverDTO;
import rw.bk.taxi24.api.service.mapper.DriverMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Driver.
 */
@Service
@Transactional
public class DriverService {

    private final Logger log = LoggerFactory.getLogger(DriverService.class);

    private final DriverRepository driverRepository;

    private final DriverMapper driverMapper;

    public DriverService(DriverRepository driverRepository, DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
    }

    /**
     * Save a driver.
     *
     * @param driverDTO the entity to requestTrip
     * @return the persisted entity
     */
    public DriverDTO save(DriverDTO driverDTO) {
        log.debug("Request to requestTrip Driver : {}", driverDTO);
        Driver driver = driverMapper.toEntity(driverDTO);
        driver = driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }

    /**
     * Get all the drivers.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DriverDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Drivers");
        return driverRepository.findAll(pageable)
            .map(driverMapper::toDto);
    }


    @Transactional(readOnly = true)
    public Page<DriverDTO> findAllAvailableDrivers(Pageable pageable) {
        log.debug("Request to get all available Drivers");
        return driverRepository.findByStatus(pageable, DriverStatus.AVAILABLE)
            .map(driverMapper::toDto);
    }



    /**
     * Get one driver by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<DriverDTO> findOne(Long id) {
        log.debug("Request to get Driver : {}", id);
        return driverRepository.findById(id)
            .map(driverMapper::toDto);
    }

    /**
     * Delete the driver by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Driver : {}", id);
        driverRepository.deleteById(id);
    }

    public Page<DriverDTO> findAvailableDriversWithinRadius(Pageable pageable, double latitude, double longitude, double radius) {
        log.debug("Request to get all available Drivers within " + radius + "km radius");
        Predicate<Driver> radiusFilter = driver -> ServiceUtils.kilometersBetweenCoordinates(latitude, longitude, driver.getLatitude(), driver.getLongitude()) < radius;
        Page<Driver> byStatus = driverRepository.findByStatus(pageable, DriverStatus.AVAILABLE);
        return new PageImpl<Driver>(byStatus.stream().filter(radiusFilter).collect(Collectors.toList())).map(driverMapper::toDto);
    }
}
