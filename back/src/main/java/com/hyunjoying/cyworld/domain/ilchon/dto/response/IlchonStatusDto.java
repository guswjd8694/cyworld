package com.hyunjoying.cyworld.domain.ilchon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "일촌 상태 응답 DTO")
public class IlchonStatusDto {
    @Schema(example = "ACCEPTED", description = "두 사용자 간의 일촌 상태 (ACCEPTED, PENDING_SENT, PENDING_RECEIVED, NONE)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String status;

    public IlchonStatusDto(String status) {
        this.status = status;
    }
}