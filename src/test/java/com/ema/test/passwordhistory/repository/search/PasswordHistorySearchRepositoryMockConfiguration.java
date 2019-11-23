package com.ema.test.passwordhistory.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PasswordHistorySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PasswordHistorySearchRepositoryMockConfiguration {

    @MockBean
    private PasswordHistorySearchRepository mockPasswordHistorySearchRepository;

}
