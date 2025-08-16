package com.hyunjoying.cyworld.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponseDto {
    @Schema(example = "요청이 성공적으로 처리되었습니다.", description = "API 성공 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
