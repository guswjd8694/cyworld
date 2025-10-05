package com.hyunjoying.cyworld.domain.emotion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateEmotionRequestDto {
    @Schema(example = "🌷 행복", description = "수정할 새로운 오늘의 감정", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer emotionId;
}
