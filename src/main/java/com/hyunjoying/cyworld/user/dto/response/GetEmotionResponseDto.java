package com.hyunjoying.cyworld.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEmotionResponseDto {

    @Schema(example = "💓 사랑", description = "오늘의 감정", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emotion;

    public GetEmotionResponseDto(String emotion) {
        this.emotion = emotion;
    }
}
