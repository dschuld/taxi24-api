package rw.bk.taxi24.api.web.rest;

import rw.bk.taxi24.api.Taxi24ApiApp;

import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.repository.RiderRepository;
import rw.bk.taxi24.api.service.RiderService;
import rw.bk.taxi24.api.service.dto.RiderDTO;
import rw.bk.taxi24.api.service.mapper.RiderMapper;
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

/**
 * Test class for the RiderResource REST controller.
 *
 * @see RiderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Taxi24ApiApp.class)
public class RiderResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_AMOUNT_RIDES = 1;
    private static final Integer UPDATED_AMOUNT_RIDES = 2;

    @Autowired
    private RiderRepository riderRepository;


    @Autowired
    private RiderMapper riderMapper;
    

    @Autowired
    private RiderService riderService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRiderMockMvc;

    private Rider rider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RiderResource riderResource = new RiderResource(riderService);
        this.restRiderMockMvc = MockMvcBuilders.standaloneSetup(riderResource)
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
    public static Rider createEntity(EntityManager em) {
        Rider rider = new Rider()
            .name(DEFAULT_NAME)
            .amountRides(DEFAULT_AMOUNT_RIDES);
        return rider;
    }

    @Before
    public void initTest() {
        rider = createEntity(em);
    }

    @Test
    @Transactional
    public void createRider() throws Exception {
        int databaseSizeBeforeCreate = riderRepository.findAll().size();

        // Create the Rider
        RiderDTO riderDTO = riderMapper.toDto(rider);
        restRiderMockMvc.perform(post("/api/riders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(riderDTO)))
            .andExpect(status().isCreated());

        // Validate the Rider in the database
        List<Rider> riderList = riderRepository.findAll();
        assertThat(riderList).hasSize(databaseSizeBeforeCreate + 1);
        Rider testRider = riderList.get(riderList.size() - 1);
        assertThat(testRider.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRider.getAmountRides()).isEqualTo(DEFAULT_AMOUNT_RIDES);
    }

    @Test
    @Transactional
    public void createRiderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = riderRepository.findAll().size();

        // Create the Rider with an existing ID
        rider.setId(1L);
        RiderDTO riderDTO = riderMapper.toDto(rider);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRiderMockMvc.perform(post("/api/riders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(riderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rider in the database
        List<Rider> riderList = riderRepository.findAll();
        assertThat(riderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllRiders() throws Exception {
        // Initialize the database
        riderRepository.saveAndFlush(rider);

        // Get all the riderList
        restRiderMockMvc.perform(get("/api/riders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].amountRides").value(hasItem(DEFAULT_AMOUNT_RIDES)));
    }
    

    @Test
    @Transactional
    public void getRider() throws Exception {
        // Initialize the database
        riderRepository.saveAndFlush(rider);

        // Get the rider
        restRiderMockMvc.perform(get("/api/riders/{id}", rider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(rider.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.amountRides").value(DEFAULT_AMOUNT_RIDES));
    }
    @Test
    @Transactional
    public void getNonExistingRider() throws Exception {
        // Get the rider
        restRiderMockMvc.perform(get("/api/riders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRider() throws Exception {
        // Initialize the database
        riderRepository.saveAndFlush(rider);

        int databaseSizeBeforeUpdate = riderRepository.findAll().size();

        // Update the rider
        Rider updatedRider = riderRepository.findById(rider.getId()).get();
        // Disconnect from session so that the updates on updatedRider are not directly saved in db
        em.detach(updatedRider);
        updatedRider
            .name(UPDATED_NAME)
            .amountRides(UPDATED_AMOUNT_RIDES);
        RiderDTO riderDTO = riderMapper.toDto(updatedRider);

        restRiderMockMvc.perform(put("/api/riders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(riderDTO)))
            .andExpect(status().isOk());

        // Validate the Rider in the database
        List<Rider> riderList = riderRepository.findAll();
        assertThat(riderList).hasSize(databaseSizeBeforeUpdate);
        Rider testRider = riderList.get(riderList.size() - 1);
        assertThat(testRider.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRider.getAmountRides()).isEqualTo(UPDATED_AMOUNT_RIDES);
    }

    @Test
    @Transactional
    public void updateNonExistingRider() throws Exception {
        int databaseSizeBeforeUpdate = riderRepository.findAll().size();

        // Create the Rider
        RiderDTO riderDTO = riderMapper.toDto(rider);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRiderMockMvc.perform(put("/api/riders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(riderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rider in the database
        List<Rider> riderList = riderRepository.findAll();
        assertThat(riderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRider() throws Exception {
        // Initialize the database
        riderRepository.saveAndFlush(rider);

        int databaseSizeBeforeDelete = riderRepository.findAll().size();

        // Get the rider
        restRiderMockMvc.perform(delete("/api/riders/{id}", rider.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Rider> riderList = riderRepository.findAll();
        assertThat(riderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rider.class);
        Rider rider1 = new Rider();
        rider1.setId(1L);
        Rider rider2 = new Rider();
        rider2.setId(rider1.getId());
        assertThat(rider1).isEqualTo(rider2);
        rider2.setId(2L);
        assertThat(rider1).isNotEqualTo(rider2);
        rider1.setId(null);
        assertThat(rider1).isNotEqualTo(rider2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RiderDTO.class);
        RiderDTO riderDTO1 = new RiderDTO();
        riderDTO1.setId(1L);
        RiderDTO riderDTO2 = new RiderDTO();
        assertThat(riderDTO1).isNotEqualTo(riderDTO2);
        riderDTO2.setId(riderDTO1.getId());
        assertThat(riderDTO1).isEqualTo(riderDTO2);
        riderDTO2.setId(2L);
        assertThat(riderDTO1).isNotEqualTo(riderDTO2);
        riderDTO1.setId(null);
        assertThat(riderDTO1).isNotEqualTo(riderDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(riderMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(riderMapper.fromId(null)).isNull();
    }
}
