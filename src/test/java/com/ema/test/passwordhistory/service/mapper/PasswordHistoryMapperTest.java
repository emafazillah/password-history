package com.ema.test.passwordhistory.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PasswordHistoryMapperTest {

    private PasswordHistoryMapper passwordHistoryMapper;

    @BeforeEach
    public void setUp() {
        passwordHistoryMapper = new PasswordHistoryMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(passwordHistoryMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(passwordHistoryMapper.fromId(null)).isNull();
    }
}
