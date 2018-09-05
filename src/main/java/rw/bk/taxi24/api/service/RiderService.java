package rw.bk.taxi24.api.service;

import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.repository.RiderRepository;
import rw.bk.taxi24.api.service.dto.RiderDTO;
import rw.bk.taxi24.api.service.mapper.RiderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
/**
 * Service Implementation for managing Rider.
 */
@Service
@Transactional
public class RiderService {

    private final Logger log = LoggerFactory.getLogger(RiderService.class);

    private final RiderRepository riderRepository;

    private final RiderMapper riderMapper;

    public RiderService(RiderRepository riderRepository, RiderMapper riderMapper) {
        this.riderRepository = riderRepository;
        this.riderMapper = riderMapper;
    }

    /**
     * Save a rider.
     *
     * @param riderDTO the entity to save
     * @return the persisted entity
     */
    public RiderDTO save(RiderDTO riderDTO) {
        log.debug("Request to save Rider : {}", riderDTO);
        Rider rider = riderMapper.toEntity(riderDTO);
        rider = riderRepository.save(rider);
        return riderMapper.toDto(rider);
    }

    /**
     * Get all the riders.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RiderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Riders");
        return riderRepository.findAll(pageable)
            .map(riderMapper::toDto);
    }


    /**
     * Get one rider by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<RiderDTO> findOne(Long id) {
        log.debug("Request to get Rider : {}", id);
        return riderRepository.findById(id)
            .map(riderMapper::toDto);
    }

    /**
     * Delete the rider by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Rider : {}", id);
        riderRepository.deleteById(id);
    }
}
