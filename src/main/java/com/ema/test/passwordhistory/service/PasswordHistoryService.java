package com.ema.test.passwordhistory.service;

import com.ema.test.passwordhistory.domain.PasswordHistory;
import com.ema.test.passwordhistory.repository.PasswordHistoryRepository;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;
import com.ema.test.passwordhistory.service.mapper.PasswordHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link PasswordHistory}.
 */
@Service
@Transactional
public class PasswordHistoryService {

    private final Logger log = LoggerFactory.getLogger(PasswordHistoryService.class);

    private final PasswordHistoryRepository passwordHistoryRepository;

    private final PasswordHistoryMapper passwordHistoryMapper;

    public PasswordHistoryService(PasswordHistoryRepository passwordHistoryRepository, PasswordHistoryMapper passwordHistoryMapper) {
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.passwordHistoryMapper = passwordHistoryMapper;
    }

    /**
     * Save a passwordHistory.
     *
     * @param passwordHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public PasswordHistoryDTO save(PasswordHistoryDTO passwordHistoryDTO) {
        log.debug("Request to save PasswordHistory : {}", passwordHistoryDTO);
        PasswordHistory passwordHistory = passwordHistoryMapper.toEntity(passwordHistoryDTO);
        passwordHistory = passwordHistoryRepository.save(passwordHistory);
        return passwordHistoryMapper.toDto(passwordHistory);
    }

    /**
     * Get all the passwordHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PasswordHistoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PasswordHistories");
        return passwordHistoryRepository.findAll(pageable)
            .map(passwordHistoryMapper::toDto);
    }


    /**
     * Get one passwordHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PasswordHistoryDTO> findOne(Long id) {
        log.debug("Request to get PasswordHistory : {}", id);
        return passwordHistoryRepository.findById(id)
            .map(passwordHistoryMapper::toDto);
    }

    /**
     * Delete the passwordHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PasswordHistory : {}", id);
        passwordHistoryRepository.deleteById(id);
    }
}
