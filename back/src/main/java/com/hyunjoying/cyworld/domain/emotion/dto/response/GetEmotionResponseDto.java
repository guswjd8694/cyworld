package com.hyunjoying.cyworld.domain.emotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetEmotionResponseDto {
    @Schema(example = "💓 사랑", description = "오늘의 감정", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emotion;
}
