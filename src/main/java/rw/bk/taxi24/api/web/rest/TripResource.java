package rw.bk.taxi24.api.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.data.domain.PageImpl;
import rw.bk.taxi24.api.domain.enumeration.TripStatus;
import rw.bk.taxi24.api.service.TripService;
import rw.bk.taxi24.api.service.dto.TripRequestDTO;
import rw.bk.taxi24.api.service.dto.TripUpdateDTO;
import rw.bk.taxi24.api.web.rest.errors.BadRequestAlertException;
import rw.bk.taxi24.api.web.rest.util.HeaderUtil;
import rw.bk.taxi24.api.web.rest.util.PaginationUtil;
import rw.bk.taxi24.api.service.dto.TripDTO;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Trip.
 */
@RestController
@RequestMapping("/api")
public class TripResource {

    private final Logger log = LoggerFactory.getLogger(TripResource.class);

    private static final String ENTITY_NAME = "trip";

    private final TripService tripService;

    public TripResource(TripService tripService) {
        this.tripService = tripService;
    }

    /**
     * POST  /trips : Create a new trip.
     *
     * @param tripDTO the tripDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tripDTO, or with status 400 (Bad Request) if the trip has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/trips")
    @Timed
    public ResponseEntity<TripDTO> createTrip(@RequestBody TripRequestDTO tripDTO) throws URISyntaxException {
        log.debug("REST request to requestTrip Trip : {}", tripDTO);

        TripDTO result = tripService.requestTrip(tripDTO.getDriverId(), tripDTO.getRiderId());
        return ResponseEntity.created(new URI("/api/trips/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trips : Updates an existing trip.
     *
     * @param tripUpdateDTO the tripDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tripDTO,
     * or with status 400 (Bad Request) if the tripDTO is not valid,
     * or with status 500 (Internal Server Error) if the tripDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PatchMapping("/trips/{id}")
    @Timed
    public ResponseEntity<TripDTO> updateTrip(@RequestBody TripUpdateDTO tripUpdateDTO, @PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to update Trip : {}", tripUpdateDTO);

        TripDTO tripDTO;

        switch (tripUpdateDTO.getNewStatus().toLowerCase()) {

            case "active":
                tripDTO = tripService.startTrip(id);
                break;
            case "cancelled":
                tripDTO = tripService.cancelTrip(id);
                break;
            case "completed":
                tripDTO = tripService.completeTrip(id);
                break;
            default:
                throw new BadRequestAlertException(tripUpdateDTO.getNewStatus().toLowerCase() + " is not a valid status", "Trip", "badStatus");
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, id.toString()))
            .body(tripDTO);
    }

    /**
     * GET  /trips : get all the trips.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of trips in body
     */
    @GetMapping("/trips")
    @Timed
    public ResponseEntity<List<TripDTO>> getAllTrips(Pageable pageable, @RequestParam(value = "status", required = false) String status) {
        log.debug("REST request to get a page of Trips");
        Page<TripDTO> page = new PageImpl<TripDTO>(tripService.findAll(pageable).filter(tripDTO -> status != null? tripDTO.getTripStatus().toString().toLowerCase().equals(status.toLowerCase()) : true).stream().collect(Collectors.toList()));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trips");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /trips/:id : get the "id" trip.
     *
     * @param id the id of the tripDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tripDTO, or with status 404 (Not Found)
     */
    @GetMapping("/trips/{id}")
    @Timed
    public ResponseEntity<TripDTO> getTrip(@PathVariable Long id) {
        log.debug("REST request to get Trip : {}", id);
        Optional<TripDTO> tripDTO = tripService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tripDTO);
    }

}
