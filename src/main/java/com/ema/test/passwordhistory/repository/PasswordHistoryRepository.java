package com.ema.test.passwordhistory.repository;

import com.ema.test.passwordhistory.domain.PasswordHistory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PasswordHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory,Long> {
	
	//Optional<PasswordHistory> findOneByHistoryNo1AndUserEmail(String encryptedPassword, String email);
	//Optional<PasswordHistory> findOneByHistoryNo2AndUserEmail(String encryptedPassword, String email);
	//Optional<PasswordHistory> findOneByHistoryNo3AndUserEmail(String encryptedPassword, String email);
	//Optional<PasswordHistory> findOneByHistoryNo4AndUserEmail(String encryptedPassword, String email);
	//Optional<PasswordHistory> findOneByHistoryNo5AndUserEmail(String encryptedPassword, String email);
	Optional<PasswordHistory> findOneByUserEmail(String email);
    
}
