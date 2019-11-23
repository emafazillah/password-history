package com.ema.test.passwordhistory.web.rest;

import com.ema.test.passwordhistory.service.PasswordHistoryService;
import com.ema.test.passwordhistory.web.rest.errors.BadRequestAlertException;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.ema.test.passwordhistory.domain.PasswordHistory}.
 */
@RestController
@RequestMapping("/api")
public class PasswordHistoryResource {

    private final Logger log = LoggerFactory.getLogger(PasswordHistoryResource.class);

    private static final String ENTITY_NAME = "passwordHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PasswordHistoryService passwordHistoryService;

    public PasswordHistoryResource(PasswordHistoryService passwordHistoryService) {
        this.passwordHistoryService = passwordHistoryService;
    }

    /**
     * {@code POST  /password-histories} : Create a new passwordHistory.
     *
     * @param passwordHistoryDTO the passwordHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new passwordHistoryDTO, or with status {@code 400 (Bad Request)} if the passwordHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/password-histories")
    public ResponseEntity<PasswordHistoryDTO> createPasswordHistory(@RequestBody PasswordHistoryDTO passwordHistoryDTO) throws URISyntaxException {
        log.debug("REST request to save PasswordHistory : {}", passwordHistoryDTO);
        if (passwordHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new passwordHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PasswordHistoryDTO result = passwordHistoryService.save(passwordHistoryDTO);
        return ResponseEntity.created(new URI("/api/password-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /password-histories} : Updates an existing passwordHistory.
     *
     * @param passwordHistoryDTO the passwordHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated passwordHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the passwordHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the passwordHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/password-histories")
    public ResponseEntity<PasswordHistoryDTO> updatePasswordHistory(@RequestBody PasswordHistoryDTO passwordHistoryDTO) throws URISyntaxException {
        log.debug("REST request to update PasswordHistory : {}", passwordHistoryDTO);
        if (passwordHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PasswordHistoryDTO result = passwordHistoryService.save(passwordHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, passwordHistoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /password-histories} : get all the passwordHistories.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of passwordHistories in body.
     */
    @GetMapping("/password-histories")
    public ResponseEntity<List<PasswordHistoryDTO>> getAllPasswordHistories(Pageable pageable) {
        log.debug("REST request to get a page of PasswordHistories");
        Page<PasswordHistoryDTO> page = passwordHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /password-histories/:id} : get the "id" passwordHistory.
     *
     * @param id the id of the passwordHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the passwordHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/password-histories/{id}")
    public ResponseEntity<PasswordHistoryDTO> getPasswordHistory(@PathVariable Long id) {
        log.debug("REST request to get PasswordHistory : {}", id);
        Optional<PasswordHistoryDTO> passwordHistoryDTO = passwordHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(passwordHistoryDTO);
    }

    /**
     * {@code DELETE  /password-histories/:id} : delete the "id" passwordHistory.
     *
     * @param id the id of the passwordHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/password-histories/{id}")
    public ResponseEntity<Void> deletePasswordHistory(@PathVariable Long id) {
        log.debug("REST request to delete PasswordHistory : {}", id);
        passwordHistoryService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/password-histories?query=:query} : search for the passwordHistory corresponding
     * to the query.
     *
     * @param query the query of the passwordHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/password-histories")
    public ResponseEntity<List<PasswordHistoryDTO>> searchPasswordHistories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PasswordHistories for query {}", query);
        Page<PasswordHistoryDTO> page = passwordHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
