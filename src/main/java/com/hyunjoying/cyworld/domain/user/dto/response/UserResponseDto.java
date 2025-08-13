package com.hyunjoying.cyworld.domain.user.dto.response;

import com.hyunjoying.cyworld.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Integer id;
    private final String loginId;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
    }
}