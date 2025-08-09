package com.hyunjoying.cyworld.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequestDto {
    private String loginId;
    private String email;
    private String newPassword;
}
