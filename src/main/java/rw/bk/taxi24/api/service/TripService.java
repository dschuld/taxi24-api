package rw.bk.taxi24.api.service;

import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.domain.Trip;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;
import rw.bk.taxi24.api.domain.enumeration.TripStatus;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.repository.RiderRepository;
import rw.bk.taxi24.api.repository.TripRepository;
import rw.bk.taxi24.api.service.dto.TripDTO;
import rw.bk.taxi24.api.service.mapper.TripMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service Implementation for managing Trip.
 */
@Service
@Transactional
public class TripService {

    private final Logger log = LoggerFactory.getLogger(TripService.class);

    private final TripRepository tripRepository;

    private final DriverRepository driverRepository;

    private final RiderRepository riderRepository;

    private final TripMapper tripMapper;

    public TripService(TripRepository tripRepository, TripMapper tripMapper, DriverRepository driverRepository, RiderRepository riderRepository) {
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
        this.driverRepository = driverRepository;
        this.riderRepository = riderRepository;
    }

    /**
     * Save a trip.
     *
     * @param driverId the Rider ID
     * @param riderId  the Driver ID
     * @return the persisted entity
     */
    public TripDTO requestTrip(Long driverId, Long riderId) {
        log.debug("Request to requestTrip Trip : {}, {}", driverId, riderId);

        Optional<Rider> rider = riderRepository.findById(riderId);
        Optional<Driver> driver = driverRepository.findById(driverId);

        Trip trip = new Trip().tripStatus(TripStatus.REQUESTED);
        trip.setDriver(driver.orElseThrow(() -> new NoSuchElementException("No driver with ID " + driverId + "found.")));
        trip.setRider(rider.orElseThrow(() -> new NoSuchElementException("No rider with ID " + riderId + "found.")));
        if (!trip.getDriver().getStatus().equals(DriverStatus.AVAILABLE)) {
            throw new DriverNotAvailableException(driverId);
        }

        trip = tripRepository.save(trip);
        return tripMapper.toDto(trip);
    }

    /**
     * Get all the trips.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TripDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Trips");
        return tripRepository.findAll(pageable)
            .map(tripMapper::toDto);
    }


    /**
     * Get one trip by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<TripDTO> findOne(Long id) {
        log.debug("Request to get Trip : {}", id);
        return tripRepository.findById(id)
            .map(tripMapper::toDto);
    }

    /**
     * Delete the trip by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Trip : {}", id);
        tripRepository.deleteById(id);
    }
}