package com.hyunjoying.cyworld.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    private String loginId;
    private String password;
    private String email;
    private String name;
    private String birth;
    private String gender;
    private String phone;
}
