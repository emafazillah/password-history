package com.ema.test.passwordhistory.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ema.test.passwordhistory.service.PasswordHistoryService;
import com.ema.test.passwordhistory.web.rest.util.HeaderUtil;
import com.ema.test.passwordhistory.web.rest.util.PaginationUtil;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;
import io.swagger.annotations.ApiParam;
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
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing PasswordHistory.
 */
@RestController
@RequestMapping("/api")
public class PasswordHistoryResource {

    private final Logger log = LoggerFactory.getLogger(PasswordHistoryResource.class);

    private static final String ENTITY_NAME = "passwordHistory";

    private final PasswordHistoryService passwordHistoryService;

    public PasswordHistoryResource(PasswordHistoryService passwordHistoryService) {
        this.passwordHistoryService = passwordHistoryService;
    }

    /**
     * POST  /password-histories : Create a new passwordHistory.
     *
     * @param passwordHistoryDTO the passwordHistoryDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new passwordHistoryDTO, or with status 400 (Bad Request) if the passwordHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/password-histories")
    @Timed
    public ResponseEntity<PasswordHistoryDTO> createPasswordHistory(@RequestBody PasswordHistoryDTO passwordHistoryDTO) throws URISyntaxException {
        log.debug("REST request to save PasswordHistory : {}", passwordHistoryDTO);
        if (passwordHistoryDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new passwordHistory cannot already have an ID")).body(null);
        }
        PasswordHistoryDTO result = passwordHistoryService.save(passwordHistoryDTO);
        return ResponseEntity.created(new URI("/api/password-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /password-histories : Updates an existing passwordHistory.
     *
     * @param passwordHistoryDTO the passwordHistoryDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated passwordHistoryDTO,
     * or with status 400 (Bad Request) if the passwordHistoryDTO is not valid,
     * or with status 500 (Internal Server Error) if the passwordHistoryDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/password-histories")
    @Timed
    public ResponseEntity<PasswordHistoryDTO> updatePasswordHistory(@RequestBody PasswordHistoryDTO passwordHistoryDTO) throws URISyntaxException {
        log.debug("REST request to update PasswordHistory : {}", passwordHistoryDTO);
        if (passwordHistoryDTO.getId() == null) {
            return createPasswordHistory(passwordHistoryDTO);
        }
        PasswordHistoryDTO result = passwordHistoryService.save(passwordHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, passwordHistoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /password-histories : get all the passwordHistories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of passwordHistories in body
     */
    @GetMapping("/password-histories")
    @Timed
    public ResponseEntity<List<PasswordHistoryDTO>> getAllPasswordHistories(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of PasswordHistories");
        Page<PasswordHistoryDTO> page = passwordHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/password-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /password-histories/:id : get the "id" passwordHistory.
     *
     * @param id the id of the passwordHistoryDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the passwordHistoryDTO, or with status 404 (Not Found)
     */
    @GetMapping("/password-histories/{id}")
    @Timed
    public ResponseEntity<PasswordHistoryDTO> getPasswordHistory(@PathVariable Long id) {
        log.debug("REST request to get PasswordHistory : {}", id);
        PasswordHistoryDTO passwordHistoryDTO = passwordHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(passwordHistoryDTO));
    }

    /**
     * DELETE  /password-histories/:id : delete the "id" passwordHistory.
     *
     * @param id the id of the passwordHistoryDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/password-histories/{id}")
    @Timed
    public ResponseEntity<Void> deletePasswordHistory(@PathVariable Long id) {
        log.debug("REST request to delete PasswordHistory : {}", id);
        passwordHistoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/password-histories?query=:query : search for the passwordHistory corresponding
     * to the query.
     *
     * @param query the query of the passwordHistory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/password-histories")
    @Timed
    public ResponseEntity<List<PasswordHistoryDTO>> searchPasswordHistories(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of PasswordHistories for query {}", query);
        Page<PasswordHistoryDTO> page = passwordHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/password-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
