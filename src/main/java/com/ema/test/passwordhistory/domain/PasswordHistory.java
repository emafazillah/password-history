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
    private String history_no1;

    @Column(name = "history_no_2")
    private String history_no2;

    @Column(name = "history_no_3")
    private String history_no3;

    @Column(name = "history_no_4")
    private String history_no4;

    @Column(name = "history_no_5")
    private String history_no5;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistory_no1() {
        return history_no1;
    }

    public PasswordHistory history_no1(String history_no1) {
        this.history_no1 = history_no1;
        return this;
    }

    public void setHistory_no1(String history_no1) {
        this.history_no1 = history_no1;
    }

    public String getHistory_no2() {
        return history_no2;
    }

    public PasswordHistory history_no2(String history_no2) {
        this.history_no2 = history_no2;
        return this;
    }

    public void setHistory_no2(String history_no2) {
        this.history_no2 = history_no2;
    }

    public String getHistory_no3() {
        return history_no3;
    }

    public PasswordHistory history_no3(String history_no3) {
        this.history_no3 = history_no3;
        return this;
    }

    public void setHistory_no3(String history_no3) {
        this.history_no3 = history_no3;
    }

    public String getHistory_no4() {
        return history_no4;
    }

    public PasswordHistory history_no4(String history_no4) {
        this.history_no4 = history_no4;
        return this;
    }

    public void setHistory_no4(String history_no4) {
        this.history_no4 = history_no4;
    }

    public String getHistory_no5() {
        return history_no5;
    }

    public PasswordHistory history_no5(String history_no5) {
        this.history_no5 = history_no5;
        return this;
    }

    public void setHistory_no5(String history_no5) {
        this.history_no5 = history_no5;
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
            ", history_no1='" + getHistory_no1() + "'" +
            ", history_no2='" + getHistory_no2() + "'" +
            ", history_no3='" + getHistory_no3() + "'" +
            ", history_no4='" + getHistory_no4() + "'" +
            ", history_no5='" + getHistory_no5() + "'" +
            "}";
    }
}
