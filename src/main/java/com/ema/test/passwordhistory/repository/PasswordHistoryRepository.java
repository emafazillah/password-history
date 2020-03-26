package com.ema.test.passwordhistory.repository;

import com.ema.test.passwordhistory.domain.PasswordHistory;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the PasswordHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory,Long> {	
	Optional<PasswordHistory> findOneByUserEmail(String email);
}
