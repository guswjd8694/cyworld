package com.hyunjoying.cyworld.domain.emotion.service;


import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.emotion.dto.request.UpdateEmotionRequestDto;
import com.hyunjoying.cyworld.domain.emotion.dto.response.GetEmotionResponseDto;
import com.hyunjoying.cyworld.domain.emotion.entity.Emotion;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.emotion.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmotionServiceImpl implements EmotionService  {
    private final EmotionRepository emotionRepository;
    private final EntityFinder entityFinder;


    @Override
    @Transactional(readOnly = true)
    public GetEmotionResponseDto getEmotion(Integer userId) {
        User user = entityFinder.getUserOrThrow(userId);
        Emotion currentEmotion = user.getEmotion();

        if (user.getEmotion() == null) {
            currentEmotion = emotionRepository.findById(1)
                    .orElseThrow(() -> new IllegalArgumentException("기본 감정(ID=1)을 찾을 수 없습니다. DB를 확인해주세요."));
        }
        return new GetEmotionResponseDto(currentEmotion.getName());
    }


    @Override
    @Transactional
    public void updateEmotion(Integer userId, UpdateEmotionRequestDto requestDto) {
        User user = entityFinder.getUserOrThrow(userId);
        Emotion newEmotion = entityFinder.getEmotionOrThrow(requestDto.getEmotionId());

        user.setEmotion(newEmotion);
    }
}
