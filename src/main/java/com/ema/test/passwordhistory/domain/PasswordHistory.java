package com.ema.test.passwordhistory.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PasswordHistory.
 */
@Entity
@Table(name = "password_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "passwordhistory")
public class PasswordHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "history_no_1")
    private String historyNo1;

    @Column(name = "history_no_2")
    private String historyNo2;

    @Column(name = "history_no_3")
    private String historyNo3;

    @Column(name = "history_no_4")
    private String historyNo4;

    @Column(name = "history_no_5")
    private String historyNo5;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistoryNo1() {
        return historyNo1;
    }

    public PasswordHistory historyNo1(String history_no1) {
        this.historyNo1 = history_no1;
        return this;
    }

    public void setHistoryNo1(String history_no1) {
        this.historyNo1 = history_no1;
    }

    public String getHistoryNo2() {
        return historyNo2;
    }

    public PasswordHistory historyNo2(String history_no2) {
        this.historyNo2 = history_no2;
        return this;
    }

    public void setHistoryNo2(String history_no2) {
        this.historyNo2 = history_no2;
    }

    public String getHistoryNo3() {
        return historyNo3;
    }

    public PasswordHistory historyNo3(String history_no3) {
        this.historyNo3 = history_no3;
        return this;
    }

    public void setHistoryNo3(String history_no3) {
        this.historyNo3 = history_no3;
    }

    public String getHistoryNo4() {
        return historyNo4;
    }

    public PasswordHistory historyNo4(String history_no4) {
        this.historyNo4 = history_no4;
        return this;
    }

    public void setHistoryNo4(String history_no4) {
        this.historyNo4 = history_no4;
    }

    public String getHistoryNo5() {
        return historyNo5;
    }

    public PasswordHistory historyNo5(String history_no5) {
        this.historyNo5 = history_no5;
        return this;
    }

    public void setHistoryNo5(String history_no5) {
        this.historyNo5 = history_no5;
    }

    public User getUser() {
        return user;
    }

    public PasswordHistory user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - Jhipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordHistory passwordHistory = (PasswordHistory) o;
        if (passwordHistory.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), passwordHistory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PasswordHistory{" +
            "id=" + getId() +
            ", history_no1='" + getHistoryNo1() + "'" +
            ", history_no2='" + getHistoryNo2() + "'" +
            ", history_no3='" + getHistoryNo3() + "'" +
            ", history_no4='" + getHistoryNo4() + "'" +
            ", history_no5='" + getHistoryNo5() + "'" +
            "}";
    }
}
