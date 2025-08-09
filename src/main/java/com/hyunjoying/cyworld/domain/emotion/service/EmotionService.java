package com.hyunjoying.cyworld.domain.emotion.service;

import com.hyunjoying.cyworld.domain.emotion.dto.request.UpdateEmotionRequestDto;
import com.hyunjoying.cyworld.domain.emotion.dto.response.GetEmotionResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface EmotionService {
    GetEmotionResponseDto getEmotion(Integer userId);
    void updateEmotion(Integer userId, UpdateEmotionRequestDto requestDto);
}
