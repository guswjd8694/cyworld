package com.hyunjoying.cyworld.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {
    @Schema(example = "newuser", description = "로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;

    @Schema(example = "password123!", description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(example = "newuser@email.com", description = "이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(example = "홍길동", description = "이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(example = "2002-02-02", description = "생년월일 (YYYY-MM-DD)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String birth;

    @Schema(example = "FEMALE", description = "성별 (MALE/FEMALE)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gender;

    @Schema(example = "010-1234-5678", description = "전화번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
}