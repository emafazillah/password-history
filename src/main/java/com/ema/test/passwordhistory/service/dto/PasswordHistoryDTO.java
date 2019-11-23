package com.ema.test.passwordhistory.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ema.test.passwordhistory.domain.PasswordHistory} entity.
 */
public class PasswordHistoryDTO implements Serializable {

    private Long id;

    private String historyNo1;

    private String historyNo2;

    private String historyNo3;

    private String historyNo4;

    private String historyNo5;


    private Long userId;

    private String userLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistoryNo1() {
        return historyNo1;
    }

    public void setHistoryNo1(String history_no1) {
        this.historyNo1 = history_no1;
    }

    public String getHistoryNo2() {
        return historyNo2;
    }

    public void setHistoryNo2(String history_no2) {
        this.historyNo2 = history_no2;
    }

    public String getHistoryNo3() {
        return historyNo3;
    }

    public void setHistoryNo3(String history_no3) {
        this.historyNo3 = history_no3;
    }

    public String getHistoryNo4() {
        return historyNo4;
    }

    public void setHistoryNo4(String history_no4) {
        this.historyNo4 = history_no4;
    }

    public String getHistoryNo5() {
        return historyNo5;
    }

    public void setHistoryNo5(String history_no5) {
        this.historyNo5 = history_no5;
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
        if (passwordHistoryDTO.getId() == null || getId() == null) {
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
            ", history_no1='" + getHistoryNo1() + "'" +
            ", history_no2='" + getHistoryNo2() + "'" +
            ", history_no3='" + getHistoryNo3() + "'" +
            ", history_no4='" + getHistoryNo4() + "'" +
            ", history_no5='" + getHistoryNo5() + "'" +
            ", user=" + getUserId() +
            ", user='" + getUserLogin() + "'" +
            "}";
    }
}
