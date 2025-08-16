package com.hyunjoying.cyworld.domain.emotion.service;

import com.hyunjoying.cyworld.domain.emotion.dto.GetEmotionListDto;
import com.hyunjoying.cyworld.domain.emotion.dto.request.UpdateEmotionRequestDto;
import com.hyunjoying.cyworld.domain.emotion.dto.response.GetEmotionResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmotionService {
    GetEmotionResponseDto getEmotion(Integer userId);
    List<GetEmotionListDto> getAllEmotions();
    void updateEmotion(Integer userId, UpdateEmotionRequestDto requestDto);
}
