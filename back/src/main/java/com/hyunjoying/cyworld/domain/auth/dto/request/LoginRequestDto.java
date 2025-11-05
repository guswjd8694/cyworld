package com.hyunjoying.cyworld.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    @Schema(example = "user1", description = "로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;

    @Schema(example = "password123!", description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}