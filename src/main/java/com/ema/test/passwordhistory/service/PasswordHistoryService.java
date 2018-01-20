package com.ema.test.passwordhistory.service;

import com.ema.test.passwordhistory.domain.PasswordHistory;
import com.ema.test.passwordhistory.repository.PasswordHistoryRepository;
import com.ema.test.passwordhistory.repository.search.PasswordHistorySearchRepository;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;
import com.ema.test.passwordhistory.service.mapper.PasswordHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;

/**
 * Service Implementation for managing PasswordHistory.
 */
@Service
@Transactional
public class PasswordHistoryService {

    private final Logger log = LoggerFactory.getLogger(PasswordHistoryService.class);

    private final PasswordHistoryRepository passwordHistoryRepository;

    private final PasswordHistoryMapper passwordHistoryMapper;

    private final PasswordHistorySearchRepository passwordHistorySearchRepository;
    
    private final PasswordEncoder passwordEncoder;

    public PasswordHistoryService(PasswordHistoryRepository passwordHistoryRepository, PasswordHistoryMapper passwordHistoryMapper, 
    		PasswordHistorySearchRepository passwordHistorySearchRepository, PasswordEncoder passwordEncoder) {
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.passwordHistoryMapper = passwordHistoryMapper;
        this.passwordHistorySearchRepository = passwordHistorySearchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save a passwordHistory.
     *
     * @param passwordHistoryDTO the entity to save
     * @return the persisted entity
     */
    public PasswordHistoryDTO save(PasswordHistoryDTO passwordHistoryDTO) {
        log.debug("Request to save PasswordHistory : {}", passwordHistoryDTO);
        PasswordHistory passwordHistory = passwordHistoryMapper.toEntity(passwordHistoryDTO);
        passwordHistory = passwordHistoryRepository.save(passwordHistory);
        PasswordHistoryDTO result = passwordHistoryMapper.toDto(passwordHistory);
        passwordHistorySearchRepository.save(passwordHistory);
        return result;
    }

    /**
     *  Get all the passwordHistories.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PasswordHistoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PasswordHistories");
        return passwordHistoryRepository.findAll(pageable)
            .map(passwordHistoryMapper::toDto);
    }

    /**
     *  Get one passwordHistory by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public PasswordHistoryDTO findOne(Long id) {
        log.debug("Request to get PasswordHistory : {}", id);
        PasswordHistory passwordHistory = passwordHistoryRepository.findOne(id);
        return passwordHistoryMapper.toDto(passwordHistory);
    }

    /**
     *  Delete the  passwordHistory by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PasswordHistory : {}", id);
        passwordHistoryRepository.delete(id);
        passwordHistorySearchRepository.delete(id);
    }

    /**
     * Search for the passwordHistory corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PasswordHistoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PasswordHistories for query {}", query);
        Page<PasswordHistory> result = passwordHistorySearchRepository.search(queryStringQuery(query), pageable);
        return result.map(passwordHistoryMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<PasswordHistoryDTO> findOneByUserEmail(String email) {
    	log.debug("Request to get existing password history", email);
    	Optional<PasswordHistory> result = passwordHistoryRepository.findOneByUserEmail(email);
    	return result.map(passwordHistoryMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Boolean isPasswordHistoryExists(Integer historyNo, String password, String email, String currentPassword) {
    	log.debug("Request to get PasswordHistory : {}", historyNo + ": Password: " + password + " Email: " + email);
    	Boolean output = false;
    	if (passwordEncoder.matches(password, currentPassword)) {
			System.out.println("HISTORY NO. " + historyNo + " PASSWORD MATCHES");
			output = true;
		} else {
			System.out.println("HISTORY NO. " + historyNo + " PASSWORD NOT MATCHES");
		}
    	return output;
    }
    
}
