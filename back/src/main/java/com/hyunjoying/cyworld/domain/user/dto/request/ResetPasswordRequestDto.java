package com.hyunjoying.cyworld.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequestDto {
    @Schema(example = "user1", description = "비밀번호를 재설정할 계정의 로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;

    @Schema(example = "khj@email.com", description = "가입 시 사용한 이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(example = "newPassword456!", description = "새로운 비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}