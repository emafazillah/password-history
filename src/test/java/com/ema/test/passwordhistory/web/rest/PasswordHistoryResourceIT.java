package com.ema.test.passwordhistory.web.rest;

import com.ema.test.passwordhistory.PasswordhistoryApp;
import com.ema.test.passwordhistory.domain.PasswordHistory;
import com.ema.test.passwordhistory.repository.PasswordHistoryRepository;
import com.ema.test.passwordhistory.repository.search.PasswordHistorySearchRepository;
import com.ema.test.passwordhistory.service.PasswordHistoryService;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;
import com.ema.test.passwordhistory.service.mapper.PasswordHistoryMapper;
import com.ema.test.passwordhistory.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static com.ema.test.passwordhistory.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PasswordHistoryResource} REST controller.
 */
@SpringBootTest(classes = PasswordhistoryApp.class)
public class PasswordHistoryResourceIT {

    private static final String DEFAULT_HISTORY_NO_1 = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_NO_1 = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_NO_2 = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_NO_2 = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_NO_3 = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_NO_3 = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_NO_4 = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_NO_4 = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_NO_5 = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_NO_5 = "BBBBBBBBBB";

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private PasswordHistoryMapper passwordHistoryMapper;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    /**
     * This repository is mocked in the com.ema.test.passwordhistory.repository.search test package.
     *
     * @see com.ema.test.passwordhistory.repository.search.PasswordHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private PasswordHistorySearchRepository mockPasswordHistorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPasswordHistoryMockMvc;

    private PasswordHistory passwordHistory;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PasswordHistoryResource passwordHistoryResource = new PasswordHistoryResource(passwordHistoryService);
        this.restPasswordHistoryMockMvc = MockMvcBuilders.standaloneSetup(passwordHistoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PasswordHistory createEntity(EntityManager em) {
        PasswordHistory passwordHistory = new PasswordHistory()
            .history_no1(DEFAULT_HISTORY_NO_1)
            .history_no2(DEFAULT_HISTORY_NO_2)
            .history_no3(DEFAULT_HISTORY_NO_3)
            .history_no4(DEFAULT_HISTORY_NO_4)
            .history_no5(DEFAULT_HISTORY_NO_5);
        return passwordHistory;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PasswordHistory createUpdatedEntity(EntityManager em) {
        PasswordHistory passwordHistory = new PasswordHistory()
            .history_no1(UPDATED_HISTORY_NO_1)
            .history_no2(UPDATED_HISTORY_NO_2)
            .history_no3(UPDATED_HISTORY_NO_3)
            .history_no4(UPDATED_HISTORY_NO_4)
            .history_no5(UPDATED_HISTORY_NO_5);
        return passwordHistory;
    }

    @BeforeEach
    public void initTest() {
        passwordHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createPasswordHistory() throws Exception {
        int databaseSizeBeforeCreate = passwordHistoryRepository.findAll().size();

        // Create the PasswordHistory
        PasswordHistoryDTO passwordHistoryDTO = passwordHistoryMapper.toDto(passwordHistory);
        restPasswordHistoryMockMvc.perform(post("/api/password-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(passwordHistoryDTO)))
            .andExpect(status().isCreated());

        // Validate the PasswordHistory in the database
        List<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findAll();
        assertThat(passwordHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        PasswordHistory testPasswordHistory = passwordHistoryList.get(passwordHistoryList.size() - 1);
        assertThat(testPasswordHistory.getHistory_no1()).isEqualTo(DEFAULT_HISTORY_NO_1);
        assertThat(testPasswordHistory.getHistory_no2()).isEqualTo(DEFAULT_HISTORY_NO_2);
        assertThat(testPasswordHistory.getHistory_no3()).isEqualTo(DEFAULT_HISTORY_NO_3);
        assertThat(testPasswordHistory.getHistory_no4()).isEqualTo(DEFAULT_HISTORY_NO_4);
        assertThat(testPasswordHistory.getHistory_no5()).isEqualTo(DEFAULT_HISTORY_NO_5);

        // Validate the PasswordHistory in Elasticsearch
        verify(mockPasswordHistorySearchRepository, times(1)).save(testPasswordHistory);
    }

    @Test
    @Transactional
    public void createPasswordHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = passwordHistoryRepository.findAll().size();

        // Create the PasswordHistory with an existing ID
        passwordHistory.setId(1L);
        PasswordHistoryDTO passwordHistoryDTO = passwordHistoryMapper.toDto(passwordHistory);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPasswordHistoryMockMvc.perform(post("/api/password-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(passwordHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PasswordHistory in the database
        List<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findAll();
        assertThat(passwordHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the PasswordHistory in Elasticsearch
        verify(mockPasswordHistorySearchRepository, times(0)).save(passwordHistory);
    }


    @Test
    @Transactional
    public void getAllPasswordHistories() throws Exception {
        // Initialize the database
        passwordHistoryRepository.saveAndFlush(passwordHistory);

        // Get all the passwordHistoryList
        restPasswordHistoryMockMvc.perform(get("/api/password-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passwordHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].history_no1").value(hasItem(DEFAULT_HISTORY_NO_1)))
            .andExpect(jsonPath("$.[*].history_no2").value(hasItem(DEFAULT_HISTORY_NO_2)))
            .andExpect(jsonPath("$.[*].history_no3").value(hasItem(DEFAULT_HISTORY_NO_3)))
            .andExpect(jsonPath("$.[*].history_no4").value(hasItem(DEFAULT_HISTORY_NO_4)))
            .andExpect(jsonPath("$.[*].history_no5").value(hasItem(DEFAULT_HISTORY_NO_5)));
    }
    
    @Test
    @Transactional
    public void getPasswordHistory() throws Exception {
        // Initialize the database
        passwordHistoryRepository.saveAndFlush(passwordHistory);

        // Get the passwordHistory
        restPasswordHistoryMockMvc.perform(get("/api/password-histories/{id}", passwordHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(passwordHistory.getId().intValue()))
            .andExpect(jsonPath("$.history_no1").value(DEFAULT_HISTORY_NO_1))
            .andExpect(jsonPath("$.history_no2").value(DEFAULT_HISTORY_NO_2))
            .andExpect(jsonPath("$.history_no3").value(DEFAULT_HISTORY_NO_3))
            .andExpect(jsonPath("$.history_no4").value(DEFAULT_HISTORY_NO_4))
            .andExpect(jsonPath("$.history_no5").value(DEFAULT_HISTORY_NO_5));
    }

    @Test
    @Transactional
    public void getNonExistingPasswordHistory() throws Exception {
        // Get the passwordHistory
        restPasswordHistoryMockMvc.perform(get("/api/password-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePasswordHistory() throws Exception {
        // Initialize the database
        passwordHistoryRepository.saveAndFlush(passwordHistory);

        int databaseSizeBeforeUpdate = passwordHistoryRepository.findAll().size();

        // Update the passwordHistory
        PasswordHistory updatedPasswordHistory = passwordHistoryRepository.findById(passwordHistory.getId()).get();
        // Disconnect from session so that the updates on updatedPasswordHistory are not directly saved in db
        em.detach(updatedPasswordHistory);
        updatedPasswordHistory
            .history_no1(UPDATED_HISTORY_NO_1)
            .history_no2(UPDATED_HISTORY_NO_2)
            .history_no3(UPDATED_HISTORY_NO_3)
            .history_no4(UPDATED_HISTORY_NO_4)
            .history_no5(UPDATED_HISTORY_NO_5);
        PasswordHistoryDTO passwordHistoryDTO = passwordHistoryMapper.toDto(updatedPasswordHistory);

        restPasswordHistoryMockMvc.perform(put("/api/password-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(passwordHistoryDTO)))
            .andExpect(status().isOk());

        // Validate the PasswordHistory in the database
        List<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findAll();
        assertThat(passwordHistoryList).hasSize(databaseSizeBeforeUpdate);
        PasswordHistory testPasswordHistory = passwordHistoryList.get(passwordHistoryList.size() - 1);
        assertThat(testPasswordHistory.getHistory_no1()).isEqualTo(UPDATED_HISTORY_NO_1);
        assertThat(testPasswordHistory.getHistory_no2()).isEqualTo(UPDATED_HISTORY_NO_2);
        assertThat(testPasswordHistory.getHistory_no3()).isEqualTo(UPDATED_HISTORY_NO_3);
        assertThat(testPasswordHistory.getHistory_no4()).isEqualTo(UPDATED_HISTORY_NO_4);
        assertThat(testPasswordHistory.getHistory_no5()).isEqualTo(UPDATED_HISTORY_NO_5);

        // Validate the PasswordHistory in Elasticsearch
        verify(mockPasswordHistorySearchRepository, times(1)).save(testPasswordHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingPasswordHistory() throws Exception {
        int databaseSizeBeforeUpdate = passwordHistoryRepository.findAll().size();

        // Create the PasswordHistory
        PasswordHistoryDTO passwordHistoryDTO = passwordHistoryMapper.toDto(passwordHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPasswordHistoryMockMvc.perform(put("/api/password-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(passwordHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PasswordHistory in the database
        List<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findAll();
        assertThat(passwordHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PasswordHistory in Elasticsearch
        verify(mockPasswordHistorySearchRepository, times(0)).save(passwordHistory);
    }

    @Test
    @Transactional
    public void deletePasswordHistory() throws Exception {
        // Initialize the database
        passwordHistoryRepository.saveAndFlush(passwordHistory);

        int databaseSizeBeforeDelete = passwordHistoryRepository.findAll().size();

        // Delete the passwordHistory
        restPasswordHistoryMockMvc.perform(delete("/api/password-histories/{id}", passwordHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findAll();
        assertThat(passwordHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PasswordHistory in Elasticsearch
        verify(mockPasswordHistorySearchRepository, times(1)).deleteById(passwordHistory.getId());
    }

    @Test
    @Transactional
    public void searchPasswordHistory() throws Exception {
        // Initialize the database
        passwordHistoryRepository.saveAndFlush(passwordHistory);
        when(mockPasswordHistorySearchRepository.search(queryStringQuery("id:" + passwordHistory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(passwordHistory), PageRequest.of(0, 1), 1));
        // Search the passwordHistory
        restPasswordHistoryMockMvc.perform(get("/api/_search/password-histories?query=id:" + passwordHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passwordHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].history_no1").value(hasItem(DEFAULT_HISTORY_NO_1)))
            .andExpect(jsonPath("$.[*].history_no2").value(hasItem(DEFAULT_HISTORY_NO_2)))
            .andExpect(jsonPath("$.[*].history_no3").value(hasItem(DEFAULT_HISTORY_NO_3)))
            .andExpect(jsonPath("$.[*].history_no4").value(hasItem(DEFAULT_HISTORY_NO_4)))
            .andExpect(jsonPath("$.[*].history_no5").value(hasItem(DEFAULT_HISTORY_NO_5)));
    }
}
