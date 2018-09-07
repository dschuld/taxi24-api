package rw.bk.taxi24.api.web.rest;

import org.junit.Ignore;
import rw.bk.taxi24.api.Taxi24ApiApp;

import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.domain.Trip;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.repository.RiderRepository;
import rw.bk.taxi24.api.repository.TripRepository;
import rw.bk.taxi24.api.service.TripService;
import rw.bk.taxi24.api.service.dto.TripDTO;
import rw.bk.taxi24.api.service.dto.TripRequestDTO;
import rw.bk.taxi24.api.service.dto.TripUpdateDTO;
import rw.bk.taxi24.api.service.mapper.TripMapper;
import rw.bk.taxi24.api.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static rw.bk.taxi24.api.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import rw.bk.taxi24.api.domain.enumeration.TripStatus;
/**
 * Test class for the TripResource REST controller.
 *
 * @see TripResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Taxi24ApiApp.class)
public class TripResourceIntTest {

    private static final Long DEFAULT_DRIVER_ID = 1l;
    private static final Long UPDATED_DRIVER_ID = 2l;

    private static final Long DEFAULT_RIDER_ID = 1l;
    private static final Long UPDATED_RIDER_ID = 2l;

    private static final TripStatus DEFAULT_TRIP_STATUS = TripStatus.REQUESTED;
    private static final TripStatus UPDATED_TRIP_STATUS = TripStatus.ACTIVE;

    private static final Float DEFAULT_DURATION = 1F;
    private static final Float UPDATED_DURATION = 2F;

    private static final Float DEFAULT_DISTANCE = 1F;
    private static final Float UPDATED_DISTANCE = 2F;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RiderRepository riderRepository;


    @Autowired
    private TripMapper tripMapper;


    @Autowired
    private TripService tripService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTripMockMvc;

    private Trip trip;

    private Driver availableDriver;

    private Driver occupiedDriver;

    private Rider rider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TripResource tripResource = new TripResource(tripService);
        this.restTripMockMvc = MockMvcBuilders.standaloneSetup(tripResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        availableDriver = new Driver().status(DriverStatus.AVAILABLE).latitude(1.1f).latitude(2.2f).name("AvailableDriver");
        occupiedDriver = new Driver().status(DriverStatus.OCCUPIED).latitude(1.1f).latitude(2.2f).name("OccupiedDriver");
        rider = new Rider().name("TheRider").amountRides(0);
        driverRepository.save(availableDriver);
        driverRepository.save(occupiedDriver);
        riderRepository.save(rider);
    }

    @Test
    @Transactional
    public void requestTrip() throws Exception {
        int databaseSizeBeforeCreate = tripRepository.findAll().size();

        Driver driver = driverRepository.findByStatus(DriverStatus.AVAILABLE).get(0);
        Rider rider = riderRepository.findAll().get(0);

        // Create the Trip
        TripRequestDTO tripRequestDTO = new TripRequestDTO(driver.getId(), rider.getId());
        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripRequestDTO)))
            .andExpect(status().isCreated());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeCreate + 1);
        Trip testTrip = tripList.get(tripList.size() - 1);
        assertThat(testTrip.getDriver().getId()).isEqualTo(driver.getId());
        assertThat(testTrip.getRider().getId()).isEqualTo(rider.getId());
        assertThat(testTrip.getTripStatus()).isEqualTo(DEFAULT_TRIP_STATUS);
    }

    @Test
    @Transactional
    public void requestTripWithOccupiedDriver() throws Exception {
        int databaseSizeBeforeCreate = tripRepository.findAll().size();

        Driver driver = driverRepository.findByStatus(DriverStatus.OCCUPIED).get(0);
        Rider rider = riderRepository.findAll().get(0);

        // Create the Trip
        TripRequestDTO tripRequestDTO = new TripRequestDTO(driver.getId(), rider.getId());
        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllTrips() throws Exception {

        Trip trip = new Trip().tripStatus(TripStatus.REQUESTED).driver(availableDriver).rider(rider);
        tripRepository.saveAndFlush(trip);
        trip = new Trip().tripStatus(TripStatus.COMPLETED).driver(occupiedDriver).rider(rider);
        tripRepository.saveAndFlush(trip);

        // Get all the tripList
        restTripMockMvc.perform(get("/api/trips?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trip.getId().intValue())))
            .andExpect(jsonPath("$.[*].driver.id").value(hasItem(availableDriver.getId().intValue())))
            .andExpect(jsonPath("$.[*].driver.id").value(hasItem(occupiedDriver.getId().intValue())))
            .andExpect(jsonPath("$.[*].rider.id").value(hasItem(rider.getId().intValue())))
            .andExpect(jsonPath("$.[*].tripStatus").value(hasItem(DEFAULT_TRIP_STATUS.toString())));
    }


    @Test
    @Transactional
    public void getTrip() throws Exception {
        Trip trip = new Trip().tripStatus(TripStatus.REQUESTED).driver(availableDriver).rider(rider);
        tripRepository.saveAndFlush(trip);

        // Get the trip
        restTripMockMvc.perform(get("/api/trips/{id}", trip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(trip.getId().intValue()))
            .andExpect(jsonPath("$.driver.id").value(availableDriver.getId().intValue()))
            .andExpect(jsonPath("$.rider.id").value(rider.getId().intValue()))
            .andExpect(jsonPath("$.tripStatus").value(TripStatus.REQUESTED.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingTrip() throws Exception {
        // Get the trip
        restTripMockMvc.perform(get("/api/trips/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void completeTrip() throws Exception {
        TripStatus newStatus = TripStatus.COMPLETED;
        TripStatus currentStatus = TripStatus.ACTIVE;
        String requestParam = "completed";
        updateTrip(newStatus, currentStatus, requestParam);
    }

    @Test
    @Transactional
    public void cancelTrip() throws Exception {
        TripStatus newStatus = TripStatus.CANCELLED;
        TripStatus currentStatus = TripStatus.REQUESTED;
        String requestParam = "cancelled";
        updateTrip(newStatus, currentStatus, requestParam);
    }

    @Test
    @Transactional
    public void startTrip() throws Exception {
        TripStatus newStatus = TripStatus.ACTIVE;
        TripStatus currentStatus = TripStatus.REQUESTED;
        String requestParam = "active";
        updateTrip(newStatus, currentStatus, requestParam);
    }

    private void updateTrip(TripStatus newStatus, TripStatus currentStatus, String requestParam) throws Exception {
        Trip trip = new Trip().tripStatus(currentStatus).driver(availableDriver).rider(rider);
        tripRepository.saveAndFlush(trip);

        int databaseSizeBeforeUpdate = tripRepository.findAll().size();

        TripUpdateDTO tripDTO = new TripUpdateDTO();
        tripDTO.setId(trip.getId());
        tripDTO.setNewStatus(requestParam);

        restTripMockMvc.perform(patch("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isOk());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeUpdate);
        Trip testTrip = tripList.get(tripList.size() - 1);
        assertThat(testTrip.getTripStatus()).isEqualTo(newStatus);
    }

    @Test
    @Transactional
    public void updateTripWithInvalidStatus() throws Exception {
        Trip trip = new Trip().tripStatus(TripStatus.REQUESTED).driver(availableDriver).rider(rider);
        tripRepository.saveAndFlush(trip);

        int databaseSizeBeforeUpdate = tripRepository.findAll().size();

        // Update the trip
        Trip updatedTrip = tripRepository.findById(trip.getId()).get();
        // Disconnect from session so that the updates on updatedTrip are not directly saved in db
        em.detach(updatedTrip);
        updatedTrip.tripStatus(TripStatus.REQUESTED);
        TripUpdateDTO tripDTO = new TripUpdateDTO();
        tripDTO.setId(trip.getId());
        tripDTO.setNewStatus("completed");

        restTripMockMvc.perform(patch("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeUpdate);
        Trip testTrip = tripList.get(tripList.size() - 1);
        assertThat(testTrip.getTripStatus()).isEqualTo(TripStatus.REQUESTED);
    }

    @Test
    @Transactional
    public void updateNonExistingTrip() throws Exception {
        int databaseSizeBeforeUpdate = tripRepository.findAll().size();

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);
        TripUpdateDTO tripUpdateDTO = new TripUpdateDTO();
        tripUpdateDTO.setId(999l);
        tripUpdateDTO.setNewStatus("completed");

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTripMockMvc.perform(patch("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripUpdateDTO)))
            .andExpect(status().isNotFound());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tripMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tripMapper.fromId(null)).isNull();
    }
}
