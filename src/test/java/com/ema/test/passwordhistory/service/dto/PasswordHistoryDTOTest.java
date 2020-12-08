package com.ema.test.passwordhistory.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.ema.test.passwordhistory.web.rest.TestUtil;

public class PasswordHistoryDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PasswordHistoryDTO.class);
        PasswordHistoryDTO passwordHistoryDTO1 = new PasswordHistoryDTO();
        passwordHistoryDTO1.setId(1L);
        PasswordHistoryDTO passwordHistoryDTO2 = new PasswordHistoryDTO();
        assertThat(passwordHistoryDTO1).isNotEqualTo(passwordHistoryDTO2);
        passwordHistoryDTO2.setId(passwordHistoryDTO1.getId());
        assertThat(passwordHistoryDTO1).isEqualTo(passwordHistoryDTO2);
        passwordHistoryDTO2.setId(2L);
        assertThat(passwordHistoryDTO1).isNotEqualTo(passwordHistoryDTO2);
        passwordHistoryDTO1.setId(null);
        assertThat(passwordHistoryDTO1).isNotEqualTo(passwordHistoryDTO2);
    }
}
