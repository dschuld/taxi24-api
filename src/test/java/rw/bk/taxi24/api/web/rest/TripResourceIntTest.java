package rw.bk.taxi24.api.web.rest;

import org.junit.Ignore;
import rw.bk.taxi24.api.Taxi24ApiApp;

import rw.bk.taxi24.api.domain.Trip;
import rw.bk.taxi24.api.repository.TripRepository;
import rw.bk.taxi24.api.service.TripService;
import rw.bk.taxi24.api.service.dto.TripDTO;
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


import static rw.bk.taxi24.api.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
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
@Ignore
public class TripResourceIntTest {

    private static final Integer DEFAULT_DRIVER_ID = 1;
    private static final Integer UPDATED_DRIVER_ID = 2;

    private static final Integer DEFAULT_RIDER_ID = 1;
    private static final Integer UPDATED_RIDER_ID = 2;

    private static final TripStatus DEFAULT_TRIP_STATUS = TripStatus.REQUESTED;
    private static final TripStatus UPDATED_TRIP_STATUS = TripStatus.ACTIVE;

    private static final Float DEFAULT_DURATION = 1F;
    private static final Float UPDATED_DURATION = 2F;

    private static final Float DEFAULT_DISTANCE = 1F;
    private static final Float UPDATED_DISTANCE = 2F;

    @Autowired
    private TripRepository tripRepository;


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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TripResource tripResource = new TripResource(tripService);
        this.restTripMockMvc = MockMvcBuilders.standaloneSetup(tripResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trip createEntity(EntityManager em) {
        Trip trip = new Trip()
//            .driverId(DEFAULT_DRIVER_ID)
//            .riderId(DEFAULT_RIDER_ID)
            .tripStatus(DEFAULT_TRIP_STATUS)
            .duration(DEFAULT_DURATION)
            .distance(DEFAULT_DISTANCE);
        return trip;
    }

    @Before
    public void initTest() {
        trip = createEntity(em);
    }

    @Test
    @Transactional
    public void createTrip() throws Exception {
        int databaseSizeBeforeCreate = tripRepository.findAll().size();

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);
        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isCreated());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeCreate + 1);
        Trip testTrip = tripList.get(tripList.size() - 1);
//        assertThat(testTrip.getDriverId()).isEqualTo(DEFAULT_DRIVER_ID);
//        assertThat(testTrip.getRiderId()).isEqualTo(DEFAULT_RIDER_ID);
        assertThat(testTrip.getTripStatus()).isEqualTo(DEFAULT_TRIP_STATUS);
        assertThat(testTrip.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testTrip.getDistance()).isEqualTo(DEFAULT_DISTANCE);
    }

    @Test
    @Transactional
    public void createTripWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tripRepository.findAll().size();

        // Create the Trip with an existing ID
        trip.setId(1L);
        TripDTO tripDTO = tripMapper.toDto(trip);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTrips() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);

        // Get all the tripList
        restTripMockMvc.perform(get("/api/trips?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trip.getId().intValue())))
            .andExpect(jsonPath("$.[*].driverId").value(hasItem(DEFAULT_DRIVER_ID)))
            .andExpect(jsonPath("$.[*].riderId").value(hasItem(DEFAULT_RIDER_ID)))
            .andExpect(jsonPath("$.[*].tripStatus").value(hasItem(DEFAULT_TRIP_STATUS.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.doubleValue())))
            .andExpect(jsonPath("$.[*].distance").value(hasItem(DEFAULT_DISTANCE.doubleValue())));
    }


    @Test
    @Transactional
    public void getTrip() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);

        // Get the trip
        restTripMockMvc.perform(get("/api/trips/{id}", trip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(trip.getId().intValue()))
            .andExpect(jsonPath("$.driverId").value(DEFAULT_DRIVER_ID))
            .andExpect(jsonPath("$.riderId").value(DEFAULT_RIDER_ID))
            .andExpect(jsonPath("$.tripStatus").value(DEFAULT_TRIP_STATUS.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.doubleValue()))
            .andExpect(jsonPath("$.distance").value(DEFAULT_DISTANCE.doubleValue()));
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
    public void updateTrip() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);

        int databaseSizeBeforeUpdate = tripRepository.findAll().size();

        // Update the trip
        Trip updatedTrip = tripRepository.findById(trip.getId()).get();
        // Disconnect from session so that the updates on updatedTrip are not directly saved in db
        em.detach(updatedTrip);
        updatedTrip
//            .driverId(UPDATED_DRIVER_ID)
//            .riderId(UPDATED_RIDER_ID)
            .tripStatus(UPDATED_TRIP_STATUS)
            .duration(UPDATED_DURATION)
            .distance(UPDATED_DISTANCE);
        TripDTO tripDTO = tripMapper.toDto(updatedTrip);

        restTripMockMvc.perform(put("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isOk());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeUpdate);
        Trip testTrip = tripList.get(tripList.size() - 1);
//        assertThat(testTrip.getDriverId()).isEqualTo(UPDATED_DRIVER_ID);
//        assertThat(testTrip.getRiderId()).isEqualTo(UPDATED_RIDER_ID);
        assertThat(testTrip.getTripStatus()).isEqualTo(UPDATED_TRIP_STATUS);
        assertThat(testTrip.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testTrip.getDistance()).isEqualTo(UPDATED_DISTANCE);
    }

    @Test
    @Transactional
    public void updateNonExistingTrip() throws Exception {
        int databaseSizeBeforeUpdate = tripRepository.findAll().size();

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTripMockMvc.perform(put("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTrip() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);

        int databaseSizeBeforeDelete = tripRepository.findAll().size();

        // Get the trip
        restTripMockMvc.perform(delete("/api/trips/{id}", trip.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trip.class);
        Trip trip1 = new Trip();
        trip1.setId(1L);
        Trip trip2 = new Trip();
        trip2.setId(trip1.getId());
        assertThat(trip1).isEqualTo(trip2);
        trip2.setId(2L);
        assertThat(trip1).isNotEqualTo(trip2);
        trip1.setId(null);
        assertThat(trip1).isNotEqualTo(trip2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TripDTO.class);
        TripDTO tripDTO1 = new TripDTO();
        tripDTO1.setId(1L);
        TripDTO tripDTO2 = new TripDTO();
        assertThat(tripDTO1).isNotEqualTo(tripDTO2);
        tripDTO2.setId(tripDTO1.getId());
        assertThat(tripDTO1).isEqualTo(tripDTO2);
        tripDTO2.setId(2L);
        assertThat(tripDTO1).isNotEqualTo(tripDTO2);
        tripDTO1.setId(null);
        assertThat(tripDTO1).isNotEqualTo(tripDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tripMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tripMapper.fromId(null)).isNull();
    }
}
