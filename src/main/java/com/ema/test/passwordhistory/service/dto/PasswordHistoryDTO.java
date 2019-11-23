package com.ema.test.passwordhistory.service.dto;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the PasswordHistory entity.
 */
public class PasswordHistoryDTO implements Serializable {

    private Long id;

    private String history_no1;

    private String history_no2;

    private String history_no3;

    private String history_no4;

    private String history_no5;

    private Long userId;

    private String userLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistory_no1() {
        return history_no1;
    }

    public void setHistory_no1(String history_no1) {
        this.history_no1 = history_no1;
    }

    public String getHistory_no2() {
        return history_no2;
    }

    public void setHistory_no2(String history_no2) {
        this.history_no2 = history_no2;
    }

    public String getHistory_no3() {
        return history_no3;
    }

    public void setHistory_no3(String history_no3) {
        this.history_no3 = history_no3;
    }

    public String getHistory_no4() {
        return history_no4;
    }

    public void setHistory_no4(String history_no4) {
        this.history_no4 = history_no4;
    }

    public String getHistory_no5() {
        return history_no5;
    }

    public void setHistory_no5(String history_no5) {
        this.history_no5 = history_no5;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PasswordHistoryDTO passwordHistoryDTO = (PasswordHistoryDTO) o;
        if(passwordHistoryDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), passwordHistoryDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PasswordHistoryDTO{" +
            "id=" + getId() +
            ", history_no1='" + getHistory_no1() + "'" +
            ", history_no2='" + getHistory_no2() + "'" +
            ", history_no3='" + getHistory_no3() + "'" +
            ", history_no4='" + getHistory_no4() + "'" +
            ", history_no5='" + getHistory_no5() + "'" +
            "}";
    }
}
