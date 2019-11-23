package com.ema.test.passwordhistory.repository;

import com.ema.test.passwordhistory.domain.PasswordHistory;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PasswordHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory,Long> {
    
}
