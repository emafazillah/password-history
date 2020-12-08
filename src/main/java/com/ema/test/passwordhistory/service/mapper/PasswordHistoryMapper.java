package com.ema.test.passwordhistory.service.mapper;


import com.ema.test.passwordhistory.domain.*;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link PasswordHistory} and its DTO {@link PasswordHistoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PasswordHistoryMapper extends EntityMapper<PasswordHistoryDTO, PasswordHistory> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    PasswordHistoryDTO toDto(PasswordHistory passwordHistory);

    @Mapping(source = "userId", target = "user")
    PasswordHistory toEntity(PasswordHistoryDTO passwordHistoryDTO);

    default PasswordHistory fromId(Long id) {
        if (id == null) {
            return null;
        }
        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setId(id);
        return passwordHistory;
    }
}
