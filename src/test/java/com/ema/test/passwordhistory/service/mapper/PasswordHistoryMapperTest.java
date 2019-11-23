package com.ema.test.passwordhistory.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ema.test.passwordhistory.domain.PasswordHistory;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


public class PasswordHistoryMapperTest {

    private PasswordHistoryMapper passwordHistoryMapper;

    @BeforeEach
    public void setUp() {
        passwordHistoryMapper = new PasswordHistoryMapper() {
			
			@Override
			public List<PasswordHistory> toEntity(List<PasswordHistoryDTO> dtoList) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<PasswordHistoryDTO> toDto(List<PasswordHistory> entityList) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public PasswordHistory toEntity(PasswordHistoryDTO passwordHistoryDTO) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public PasswordHistoryDTO toDto(PasswordHistory passwordHistory) {
				// TODO Auto-generated method stub
				return null;
			}
		};
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(passwordHistoryMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(passwordHistoryMapper.fromId(null)).isNull();
    }
}
