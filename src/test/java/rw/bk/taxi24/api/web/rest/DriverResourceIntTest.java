package rw.bk.taxi24.api.web.rest;

import rw.bk.taxi24.api.Taxi24ApiApp;

import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.repository.RiderRepository;
import rw.bk.taxi24.api.service.DriverService;
import rw.bk.taxi24.api.service.dto.DriverDTO;
import rw.bk.taxi24.api.service.mapper.DriverMapper;
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

/**
 * Test class for the DriverResource REST controller.
 *
 * @see DriverResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Taxi24ApiApp.class)
public class DriverResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double KIMI_LATITUDE = -1.9544746615897D;
    private static final Double AIRPORT_LATITUDE = -1.962992861040D;

    private static final Double KIMI_LONGITUDE = 30.08274669333D;
    private static final Double AIRPORT_LONGITUDE = 30.13498306274D;

    private static final DriverStatus DEFAULT_STATUS = DriverStatus.AVAILABLE;
    private static final DriverStatus UPDATED_STATUS = DriverStatus.OCCUPIED;

    @Autowired
    private DriverRepository driverRepository;


    @Autowired
    private RiderRepository riderRepository;


    @Autowired
    private DriverMapper driverMapper;


    @Autowired
    private DriverService driverService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDriverMockMvc;

    private Driver driver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DriverResource driverResource = new DriverResource(driverService);
        this.restDriverMockMvc = MockMvcBuilders.standaloneSetup(driverResource)
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
    public static Driver createEntity(EntityManager em) {
        Driver driver = new Driver()
            .name(DEFAULT_NAME)
            .latitude(KIMI_LATITUDE)
            .longitude(KIMI_LONGITUDE)
            .status(DEFAULT_STATUS);
        return driver;
    }

    @Before
    public void initTest() {
        driver = createEntity(em);
    }

    @Test
    @Transactional
    public void createDriver() throws Exception {
        int databaseSizeBeforeCreate = driverRepository.findAll().size();

        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);
        restDriverMockMvc.perform(post("/api/drivers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(driverDTO)))
            .andExpect(status().isCreated());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeCreate + 1);
        Driver testDriver = driverList.get(driverList.size() - 1);
        assertThat(testDriver.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDriver.getLatitude()).isEqualTo(KIMI_LATITUDE);
        assertThat(testDriver.getLongitude()).isEqualTo(KIMI_LONGITUDE);
        assertThat(testDriver.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createDriverWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = driverRepository.findAll().size();

        // Create the Driver with an existing ID
        driver.setId(1L);
        DriverDTO driverDTO = driverMapper.toDto(driver);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDriverMockMvc.perform(post("/api/drivers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(driverDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllDrivers() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        // Get all the driverList
        restDriverMockMvc.perform(get("/api/drivers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(driver.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(KIMI_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(KIMI_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getAllAvailableDrivers() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);
        Driver occupiedDriver = createEntity(this.em);
        occupiedDriver.setStatus(DriverStatus.OCCUPIED);
        String occupiedName = "Occupied";
        occupiedDriver.setName(occupiedName);
        driverRepository.saveAndFlush(occupiedDriver);


        // Get all the driverList
        restDriverMockMvc.perform(get("/api/drivers?sort=id,desc&status=available"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(driver.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].name").value(not(hasItem(occupiedName))))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(KIMI_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(KIMI_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DriverStatus.AVAILABLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(not(hasItem(DriverStatus.OCCUPIED.toString()))));
    }


    @Test
    @Transactional
    public void getAllAvailableDriversInDefaultRadius() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);
        Driver farAwayDriver = createEntity(this.em);
        farAwayDriver.setStatus(DriverStatus.AVAILABLE);
        farAwayDriver.setLatitude(10D);
        farAwayDriver.setLongitude(10D);
        farAwayDriver.setName("FarAway");
        driverRepository.saveAndFlush(farAwayDriver);


        // Get all the driverList
        restDriverMockMvc.perform(get("/api/drivers?sort=id,desc&status=available&latitude=-1.9532425295779272&longitude=30.093203119591976"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(driver.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].name").value(not(hasItem(farAwayDriver.getName()))))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(KIMI_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(not(hasItem(farAwayDriver.getLongitude().doubleValue()))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DriverStatus.AVAILABLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(not(hasItem(DriverStatus.OCCUPIED.toString()))));
    }


    @Test
    @Transactional
    public void get3AvailableDriversForRiderId() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);
        Driver farAwayDriver = createEntity(this.em);
        farAwayDriver.status(DriverStatus.AVAILABLE).latitude(10D).longitude(10D).name("FarAway");
        Driver veryFarAwayDriver = createEntity(this.em);
        veryFarAwayDriver.status(DriverStatus.AVAILABLE).latitude(50D).longitude(-50D).name("VeryFarAway");
        Driver northPoleDriver = createEntity(this.em);
        northPoleDriver.status(DriverStatus.AVAILABLE).latitude(89D).longitude(10D).name("NorthPole");
        driverRepository.saveAndFlush(farAwayDriver);
        driverRepository.saveAndFlush(veryFarAwayDriver);
        driverRepository.saveAndFlush(northPoleDriver);
        Rider rider = new Rider().name("Rider").latitude(-1.9532425295779272D).longitude(30.093203119591976D);
        riderRepository.saveAndFlush(rider);


        // Get all the driverList
        restDriverMockMvc.perform(get("/api/drivers?sort=id,desc&riderId=" + rider.getId()))
            .andExpect(status().isOk()).andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(driver.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(farAwayDriver.getName())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(veryFarAwayDriver.getName())))
            .andExpect(jsonPath("$.[*].name").value(not(hasItem(northPoleDriver.getName()))))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(KIMI_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DriverStatus.AVAILABLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(not(hasItem(DriverStatus.OCCUPIED.toString()))));
    }




    @Test
    @Transactional
    public void getDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        // Get the driver
        restDriverMockMvc.perform(get("/api/drivers/{id}", driver.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(driver.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.latitude").value(KIMI_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(KIMI_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingDriver() throws Exception {
        // Get the driver
        restDriverMockMvc.perform(get("/api/drivers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        int databaseSizeBeforeUpdate = driverRepository.findAll().size();

        // Update the driver
        Driver updatedDriver = driverRepository.findById(driver.getId()).get();
        // Disconnect from session so that the updates on updatedDriver are not directly saved in db
        em.detach(updatedDriver);
        updatedDriver
            .name(UPDATED_NAME)
            .latitude(AIRPORT_LATITUDE)
            .longitude(AIRPORT_LONGITUDE)
            .status(UPDATED_STATUS);
        DriverDTO driverDTO = driverMapper.toDto(updatedDriver);

        restDriverMockMvc.perform(put("/api/drivers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(driverDTO)))
            .andExpect(status().isOk());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);
        Driver testDriver = driverList.get(driverList.size() - 1);
        assertThat(testDriver.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDriver.getLatitude()).isEqualTo(AIRPORT_LATITUDE);
        assertThat(testDriver.getLongitude()).isEqualTo(AIRPORT_LONGITUDE);
        assertThat(testDriver.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingDriver() throws Exception {
        int databaseSizeBeforeUpdate = driverRepository.findAll().size();

        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDriverMockMvc.perform(put("/api/drivers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(driverDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        int databaseSizeBeforeDelete = driverRepository.findAll().size();

        // Get the driver
        restDriverMockMvc.perform(delete("/api/drivers/{id}", driver.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Driver.class);
        Driver driver1 = new Driver();
        driver1.setId(1L);
        Driver driver2 = new Driver();
        driver2.setId(driver1.getId());
        assertThat(driver1).isEqualTo(driver2);
        driver2.setId(2L);
        assertThat(driver1).isNotEqualTo(driver2);
        driver1.setId(null);
        assertThat(driver1).isNotEqualTo(driver2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DriverDTO.class);
        DriverDTO driverDTO1 = new DriverDTO();
        driverDTO1.setId(1L);
        DriverDTO driverDTO2 = new DriverDTO();
        assertThat(driverDTO1).isNotEqualTo(driverDTO2);
        driverDTO2.setId(driverDTO1.getId());
        assertThat(driverDTO1).isEqualTo(driverDTO2);
        driverDTO2.setId(2L);
        assertThat(driverDTO1).isNotEqualTo(driverDTO2);
        driverDTO1.setId(null);
        assertThat(driverDTO1).isNotEqualTo(driverDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(driverMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(driverMapper.fromId(null)).isNull();
    }
}
