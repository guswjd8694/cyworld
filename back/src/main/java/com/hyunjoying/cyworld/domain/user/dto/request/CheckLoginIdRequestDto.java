package com.hyunjoying.cyworld.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckLoginIdRequestDto {
    @Schema(example = "user1", description = "중복 확인할 로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;
}