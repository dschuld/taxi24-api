package rw.bk.taxi24.api.web.rest;

import com.codahale.metrics.annotation.Timed;
import rw.bk.taxi24.api.service.DriverService;
import rw.bk.taxi24.api.web.rest.errors.BadRequestAlertException;
import rw.bk.taxi24.api.web.rest.util.HeaderUtil;
import rw.bk.taxi24.api.web.rest.util.PaginationUtil;
import rw.bk.taxi24.api.service.dto.DriverDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static rw.bk.taxi24.api.domain.enumeration.DriverStatus.AVAILABLE;

/**
 * REST controller for managing Driver.
 */
@RestController
@RequestMapping("/api")
public class DriverResource {

    private final Logger log = LoggerFactory.getLogger(DriverResource.class);

    private static final String ENTITY_NAME = "driver";

    private final DriverService driverService;

    public DriverResource(DriverService driverService) {
        this.driverService = driverService;
    }

    /**
     * POST  /drivers : Create a new driver.
     *
     * @param driverDTO the driverDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new driverDTO, or with status 400 (Bad Request) if the driver has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/drivers")
    @Timed
    public ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO) throws URISyntaxException {
        log.debug("REST request to requestTrip Driver : {}", driverDTO);
        if (driverDTO.getId() != null) {
            throw new BadRequestAlertException("A new driver cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DriverDTO result = driverService.save(driverDTO);
        return ResponseEntity.created(new URI("/api/drivers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /drivers : Updates an existing driver.
     *
     * @param driverDTO the driverDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated driverDTO,
     * or with status 400 (Bad Request) if the driverDTO is not valid,
     * or with status 500 (Internal Server Error) if the driverDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/drivers")
    @Timed
    public ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverDTO driverDTO) throws URISyntaxException {
        log.debug("REST request to update Driver : {}", driverDTO);
        if (driverDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DriverDTO result = driverService.save(driverDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, driverDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /drivers : get all the drivers. Valid query parameters are<br>
     * <li>status : Get drivers with the given status. Currently, only AVAILABLE is valid.</li>
     * <li>latitude/longitude : Get drivers in a certain radius around the given point.</li>
     * <li>radius : Get drivers with the given radius in km. Must be used with latitude/longitude. When omitted, defaults to 3.</li>
     * <li>riderId : Find the 3 drivers closest to the rider with the given ID.</li>
     *
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of drivers in body
     */
    @GetMapping("/drivers")
    @Timed
    public ResponseEntity<List<DriverDTO>> dispatchGetDriversCall(Pageable pageable, @RequestParam Map<String, String> params) {
        log.debug("REST request to get a page of Drivers");
        try {
            if (params.containsKey("riderId")) {
                return findClosestForRider(pageable, Long.valueOf(params.get("riderId")));
            } else if (params.containsKey("latitude") && params.containsKey("longitude")) {
                return findWithinRadius(pageable, params);
            } else if (params.containsKey("status") && params.get("status").toLowerCase().equals(AVAILABLE.toString().toLowerCase())) {
                return findAvailable(pageable);
            } else {
                return findAll(pageable);
            }
        } catch (NumberFormatException e) {
            throw new BadRequestAlertException(e.getMessage(), "Driver", "WrongNumberFormat");
        }
    }

    private ResponseEntity<List<DriverDTO>> findClosestForRider(Pageable pageable, Long riderId) {

        Page<DriverDTO> page = driverService.findClosestDriversForRider(pageable, riderId, 3);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/drivers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private ResponseEntity<List<DriverDTO>> findAvailable(Pageable pageable) {

        Page<DriverDTO> page = driverService.findAllAvailableDrivers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/drivers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private ResponseEntity<List<DriverDTO>> findWithinRadius(Pageable pageable, Map<String, String> params) {

        Double latitude = Double.valueOf(params.get("latitude"));
        Double longitude = Double.valueOf(params.get("longitude"));
        Double radius = params.containsKey("radius") ? Double.valueOf(params.get("radius")) : 3D;
        Page<DriverDTO> page = driverService.findAvailableDriversWithinRadius(pageable, latitude, longitude, radius);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/drivers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

    }

    private ResponseEntity<List<DriverDTO>> findAll(Pageable pageable) {
        Page<DriverDTO> page = driverService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/drivers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /drivers/:id : get the "id" driver.
     *
     * @param id the id of the driverDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the driverDTO, or with status 404 (Not Found)
     */
    @GetMapping("/drivers/{id}")
    @Timed
    public ResponseEntity<DriverDTO> getDriver(@PathVariable Long id) {
        log.debug("REST request to get Driver : {}", id);
        Optional<DriverDTO> driverDTO = driverService.findOne(id);
        return ResponseUtil.wrapOrNotFound(driverDTO);
    }

    /**
     * DELETE  /drivers/:id : delete the "id" driver.
     *
     * @param id the id of the driverDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/drivers/{id}")
    @Timed
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        log.debug("REST request to delete Driver : {}", id);
        driverService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
