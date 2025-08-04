package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.UpdateEmotionRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetEmotionResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface EmotionService {
    GetEmotionResponseDto getEmotion(Integer userId);
    void updateEmotion(Integer userId, UpdateEmotionRequestDto requestDto);
}
