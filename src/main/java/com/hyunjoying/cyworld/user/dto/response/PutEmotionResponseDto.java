package com.hyunjoying.cyworld.user.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutEmotionResponseDto {
    @Schema(example = "🤬 개빡침", description = "오늘의 감정 수정", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emotion;

    public PutEmotionResponseDto(String emotion) {
        this.emotion = emotion;
    }

}
