package com.hyunjoying.cyworld.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindLoginIdRequestDto {
    @Schema(example = "김현정", description = "아이디를 찾을 사용자의 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(example = "khj@email.com", description = "아이디를 찾을 사용자의 이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}