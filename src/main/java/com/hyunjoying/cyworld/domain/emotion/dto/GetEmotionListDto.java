package com.hyunjoying.cyworld.domain.emotion.dto;

import com.hyunjoying.cyworld.domain.emotion.entity.Emotion;
import lombok.Getter;

@Getter
public class GetEmotionListDto {
    private final Integer emotionId;
    private final String emotionName;

    public GetEmotionListDto(Emotion emotion) {
        this.emotionId = emotion.getId();
        this.emotionName = emotion.getName();
    }
}
