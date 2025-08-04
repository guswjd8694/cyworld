package com.hyunjoying.cyworld.user.service;


import com.hyunjoying.cyworld.user.dto.request.UpdateEmotionRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetEmotionResponseDto;
import com.hyunjoying.cyworld.user.entity.Emotion;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.repository.EmotionRepository;
import com.hyunjoying.cyworld.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmotionServiceImpl implements EmotionService  {

    @Autowired
    private EmotionRepository emotionRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public GetEmotionResponseDto getEmotion(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        Emotion currentEmotion = user.getEmotion();

        if (user.getEmotion() != null) {
            currentEmotion = emotionRepository.findById(1)
                    .orElseThrow(() -> new IllegalStateException("기본 감정(ID=1)을 찾을 수 없습니다. DB를 확인해주세요."));
        }
        return new GetEmotionResponseDto(currentEmotion.getName());
    }


    @Override
    @Transactional
    public void updateEmotion(Integer userId, UpdateEmotionRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        Emotion newEmotion = emotionRepository.findById(requestDto.getEmotionId())
                .orElseThrow(() -> new RuntimeException("선택한 감정을 찾을 수 없습니다: " + requestDto.getEmotionId()));

        user.setEmotion(newEmotion);
    }
}
