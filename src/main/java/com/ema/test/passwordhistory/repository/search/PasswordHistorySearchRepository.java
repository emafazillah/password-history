package com.ema.test.passwordhistory.repository.search;
import com.ema.test.passwordhistory.domain.PasswordHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link PasswordHistory} entity.
 */
public interface PasswordHistorySearchRepository extends ElasticsearchRepository<PasswordHistory, Long> {
}
