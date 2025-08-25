package com.hyunjoying.cyworld.domain.user.dto.response;

import com.hyunjoying.cyworld.domain.user.entity.User;
import lombok.Getter;

@Getter
public class GetUserResponseDto {
    private final Integer id;
    private final String loginId;
    private final String name;
    private String email;
    private String phone;

    public GetUserResponseDto(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}