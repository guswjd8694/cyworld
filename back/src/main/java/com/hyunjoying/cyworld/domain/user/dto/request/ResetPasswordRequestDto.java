package com.hyunjoying.cyworld.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequestDto {
    private String loginId;
    private String email;
    private String newPassword;
}
