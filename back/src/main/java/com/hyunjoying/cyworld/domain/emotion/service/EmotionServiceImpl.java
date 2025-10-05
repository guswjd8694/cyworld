package com.hyunjoying.cyworld.domain.emotion.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.emotion.dto.GetEmotionListDto;
import com.hyunjoying.cyworld.domain.emotion.dto.request.UpdateEmotionRequestDto;
import com.hyunjoying.cyworld.domain.emotion.dto.response.GetEmotionResponseDto;
import com.hyunjoying.cyworld.domain.emotion.entity.Emotion;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.emotion.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionServiceImpl implements EmotionService {
    private final EmotionRepository emotionRepository;
    private final EntityFinder entityFinder;

    @Override
    @Transactional(readOnly = true)
    public GetEmotionResponseDto getEmotion(Integer userId) {
        User user = entityFinder.getUserOrThrow(userId);
        Emotion currentEmotion = user.getEmotion();

        if (currentEmotion == null) {
            currentEmotion = entityFinder.getEmotionOrThrow(1);
        }
        return new GetEmotionResponseDto(currentEmotion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEmotionListDto> getAllEmotions() {
        return emotionRepository.findAll().stream()
                .map(GetEmotionListDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateEmotion(Integer userId, UpdateEmotionRequestDto requestDto) {
        User user = entityFinder.getUserOrThrow(userId);
        Emotion newEmotion = entityFinder.getEmotionOrThrow(requestDto.getEmotionId());

        user.updateEmotion(newEmotion);
    }
}
