package com.ema.test.passwordhistory.web.rest;

import com.ema.test.passwordhistory.PasswordhistoryApp;

import com.ema.test.passwordhistory.domain.PasswordHistory;
import com.ema.test.passwordhistory.repository.PasswordHistoryRepository;
import com.ema.test.passwordhistory.service.PasswordHistoryService;
import com.ema.test.passwordhistory.repository.search.PasswordHistorySearchRepository;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;
import com.ema.test.passwordhistory.service.mapper.PasswordHistoryMapper;
import com.ema.test.passwordhistory.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PasswordHistoryResource REST controller.
 *
 * @see PasswordHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PasswordhistoryApp.class)
public class PasswordHistoryResourceIntTest {

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

    @Autowired
    private PasswordHistorySearchRepository passwordHistorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPasswordHistoryMockMvc;

    private PasswordHistory passwordHistory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PasswordHistoryResource passwordHistoryResource = new PasswordHistoryResource(passwordHistoryService);
        this.restPasswordHistoryMockMvc = MockMvcBuilders.standaloneSetup(passwordHistoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PasswordHistory createEntity(EntityManager em) {
        PasswordHistory passwordHistory = new PasswordHistory()
            .historyNo1(DEFAULT_HISTORY_NO_1)
            .historyNo2(DEFAULT_HISTORY_NO_2)
            .historyNo3(DEFAULT_HISTORY_NO_3)
            .historyNo4(DEFAULT_HISTORY_NO_4)
            .historyNo5(DEFAULT_HISTORY_NO_5);
        return passwordHistory;
    }

    @Before
    public void initTest() {
        passwordHistorySearchRepository.deleteAll();
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
        assertThat(testPasswordHistory.getHistoryNo1()).isEqualTo(DEFAULT_HISTORY_NO_1);
        assertThat(testPasswordHistory.getHistoryNo2()).isEqualTo(DEFAULT_HISTORY_NO_2);
        assertThat(testPasswordHistory.getHistoryNo3()).isEqualTo(DEFAULT_HISTORY_NO_3);
        assertThat(testPasswordHistory.getHistoryNo4()).isEqualTo(DEFAULT_HISTORY_NO_4);
        assertThat(testPasswordHistory.getHistoryNo5()).isEqualTo(DEFAULT_HISTORY_NO_5);

        // Validate the PasswordHistory in Elasticsearch
        PasswordHistory passwordHistoryEs = passwordHistorySearchRepository.findOne(testPasswordHistory.getId());
        assertThat(passwordHistoryEs).isEqualToComparingFieldByField(testPasswordHistory);
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
            .andExpect(jsonPath("$.[*].history_no1").value(hasItem(DEFAULT_HISTORY_NO_1.toString())))
            .andExpect(jsonPath("$.[*].history_no2").value(hasItem(DEFAULT_HISTORY_NO_2.toString())))
            .andExpect(jsonPath("$.[*].history_no3").value(hasItem(DEFAULT_HISTORY_NO_3.toString())))
            .andExpect(jsonPath("$.[*].history_no4").value(hasItem(DEFAULT_HISTORY_NO_4.toString())))
            .andExpect(jsonPath("$.[*].history_no5").value(hasItem(DEFAULT_HISTORY_NO_5.toString())));
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
            .andExpect(jsonPath("$.history_no1").value(DEFAULT_HISTORY_NO_1.toString()))
            .andExpect(jsonPath("$.history_no2").value(DEFAULT_HISTORY_NO_2.toString()))
            .andExpect(jsonPath("$.history_no3").value(DEFAULT_HISTORY_NO_3.toString()))
            .andExpect(jsonPath("$.history_no4").value(DEFAULT_HISTORY_NO_4.toString()))
            .andExpect(jsonPath("$.history_no5").value(DEFAULT_HISTORY_NO_5.toString()));
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
        passwordHistorySearchRepository.save(passwordHistory);
        int databaseSizeBeforeUpdate = passwordHistoryRepository.findAll().size();

        // Update the passwordHistory
        PasswordHistory updatedPasswordHistory = passwordHistoryRepository.findOne(passwordHistory.getId());
        updatedPasswordHistory
            .historyNo1(UPDATED_HISTORY_NO_1)
            .historyNo2(UPDATED_HISTORY_NO_2)
            .historyNo3(UPDATED_HISTORY_NO_3)
            .historyNo4(UPDATED_HISTORY_NO_4)
            .historyNo5(UPDATED_HISTORY_NO_5);
        PasswordHistoryDTO passwordHistoryDTO = passwordHistoryMapper.toDto(updatedPasswordHistory);

        restPasswordHistoryMockMvc.perform(put("/api/password-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(passwordHistoryDTO)))
            .andExpect(status().isOk());

        // Validate the PasswordHistory in the database
        List<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findAll();
        assertThat(passwordHistoryList).hasSize(databaseSizeBeforeUpdate);
        PasswordHistory testPasswordHistory = passwordHistoryList.get(passwordHistoryList.size() - 1);
        assertThat(testPasswordHistory.getHistoryNo1()).isEqualTo(UPDATED_HISTORY_NO_1);
        assertThat(testPasswordHistory.getHistoryNo2()).isEqualTo(UPDATED_HISTORY_NO_2);
        assertThat(testPasswordHistory.getHistoryNo3()).isEqualTo(UPDATED_HISTORY_NO_3);
        assertThat(testPasswordHistory.getHistoryNo4()).isEqualTo(UPDATED_HISTORY_NO_4);
        assertThat(testPasswordHistory.getHistoryNo5()).isEqualTo(UPDATED_HISTORY_NO_5);

        // Validate the PasswordHistory in Elasticsearch
        PasswordHistory passwordHistoryEs = passwordHistorySearchRepository.findOne(testPasswordHistory.getId());
        assertThat(passwordHistoryEs).isEqualToComparingFieldByField(testPasswordHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingPasswordHistory() throws Exception {
        int databaseSizeBeforeUpdate = passwordHistoryRepository.findAll().size();

        // Create the PasswordHistory
        PasswordHistoryDTO passwordHistoryDTO = passwordHistoryMapper.toDto(passwordHistory);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPasswordHistoryMockMvc.perform(put("/api/password-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(passwordHistoryDTO)))
            .andExpect(status().isCreated());

        // Validate the PasswordHistory in the database
        List<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findAll();
        assertThat(passwordHistoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePasswordHistory() throws Exception {
        // Initialize the database
        passwordHistoryRepository.saveAndFlush(passwordHistory);
        passwordHistorySearchRepository.save(passwordHistory);
        int databaseSizeBeforeDelete = passwordHistoryRepository.findAll().size();

        // Get the passwordHistory
        restPasswordHistoryMockMvc.perform(delete("/api/password-histories/{id}", passwordHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean passwordHistoryExistsInEs = passwordHistorySearchRepository.exists(passwordHistory.getId());
        assertThat(passwordHistoryExistsInEs).isFalse();

        // Validate the database is empty
        List<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findAll();
        assertThat(passwordHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPasswordHistory() throws Exception {
        // Initialize the database
        passwordHistoryRepository.saveAndFlush(passwordHistory);
        passwordHistorySearchRepository.save(passwordHistory);

        // Search the passwordHistory
        restPasswordHistoryMockMvc.perform(get("/api/_search/password-histories?query=id:" + passwordHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passwordHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].history_no1").value(hasItem(DEFAULT_HISTORY_NO_1.toString())))
            .andExpect(jsonPath("$.[*].history_no2").value(hasItem(DEFAULT_HISTORY_NO_2.toString())))
            .andExpect(jsonPath("$.[*].history_no3").value(hasItem(DEFAULT_HISTORY_NO_3.toString())))
            .andExpect(jsonPath("$.[*].history_no4").value(hasItem(DEFAULT_HISTORY_NO_4.toString())))
            .andExpect(jsonPath("$.[*].history_no5").value(hasItem(DEFAULT_HISTORY_NO_5.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PasswordHistory.class);
        PasswordHistory passwordHistory1 = new PasswordHistory();
        passwordHistory1.setId(1L);
        PasswordHistory passwordHistory2 = new PasswordHistory();
        passwordHistory2.setId(passwordHistory1.getId());
        assertThat(passwordHistory1).isEqualTo(passwordHistory2);
        passwordHistory2.setId(2L);
        assertThat(passwordHistory1).isNotEqualTo(passwordHistory2);
        passwordHistory1.setId(null);
        assertThat(passwordHistory1).isNotEqualTo(passwordHistory2);
    }

    @Test
    @Transactional
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

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(passwordHistoryMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(passwordHistoryMapper.fromId(null)).isNull();
    }
}
