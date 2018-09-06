package rw.bk.taxi24.api.web.rest;

import com.codahale.metrics.annotation.Timed;
import rw.bk.taxi24.api.service.RiderService;
import rw.bk.taxi24.api.web.rest.errors.BadRequestAlertException;
import rw.bk.taxi24.api.web.rest.util.HeaderUtil;
import rw.bk.taxi24.api.web.rest.util.PaginationUtil;
import rw.bk.taxi24.api.service.dto.RiderDTO;
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
import java.util.Optional;

/**
 * REST controller for managing Rider.
 */
@RestController
@RequestMapping("/api")
public class RiderResource {

    private final Logger log = LoggerFactory.getLogger(RiderResource.class);

    private static final String ENTITY_NAME = "rider";

    private final RiderService riderService;

    public RiderResource(RiderService riderService) {
        this.riderService = riderService;
    }

    /**
     * POST  /riders : Create a new rider.
     *
     * @param riderDTO the riderDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new riderDTO, or with status 400 (Bad Request) if the rider has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/riders")
    @Timed
    public ResponseEntity<RiderDTO> createRider(@RequestBody RiderDTO riderDTO) throws URISyntaxException {
        log.debug("REST request to requestTrip Rider : {}", riderDTO);
        if (riderDTO.getId() != null) {
            throw new BadRequestAlertException("A new rider cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RiderDTO result = riderService.save(riderDTO);
        return ResponseEntity.created(new URI("/api/riders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /riders : Updates an existing rider.
     *
     * @param riderDTO the riderDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated riderDTO,
     * or with status 400 (Bad Request) if the riderDTO is not valid,
     * or with status 500 (Internal Server Error) if the riderDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/riders")
    @Timed
    public ResponseEntity<RiderDTO> updateRider(@RequestBody RiderDTO riderDTO) throws URISyntaxException {
        log.debug("REST request to update Rider : {}", riderDTO);
        if (riderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RiderDTO result = riderService.save(riderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, riderDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /riders : get all the riders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of riders in body
     */
    @GetMapping("/riders")
    @Timed
    public ResponseEntity<List<RiderDTO>> getAllRiders(Pageable pageable) {
        log.debug("REST request to get a page of Riders");
        Page<RiderDTO> page = riderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/riders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /riders/:id : get the "id" rider.
     *
     * @param id the id of the riderDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the riderDTO, or with status 404 (Not Found)
     */
    @GetMapping("/riders/{id}")
    @Timed
    public ResponseEntity<RiderDTO> getRider(@PathVariable Long id) {
        log.debug("REST request to get Rider : {}", id);
        Optional<RiderDTO> riderDTO = riderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(riderDTO);
    }

    /**
     * DELETE  /riders/:id : delete the "id" rider.
     *
     * @param id the id of the riderDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/riders/{id}")
    @Timed
    public ResponseEntity<Void> deleteRider(@PathVariable Long id) {
        log.debug("REST request to delete Rider : {}", id);
        riderService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
