package com.hyunjoying.cyworld.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String birth;
    private String gender;
}
