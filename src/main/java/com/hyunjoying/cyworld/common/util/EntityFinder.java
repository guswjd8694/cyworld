package com.hyunjoying.cyworld.common.util;

import com.hyunjoying.cyworld.user.entity.*;
import com.hyunjoying.cyworld.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityFinder {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final MinihomeRepository minihomeRepository;
    private final EmotionRepository emotionRepository;
    private final UserProfileRepository userProfileRepository;


    public User getUserOrThrow(Integer currentUserId){
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + currentUserId));
    }

    public Board getBoardOrThrow(Integer boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다: " + boardId));
    }

    public MiniHomepage getMiniHomepageByUserIdOrThrow(Integer userId) {
        return minihomeRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 미니홈피를 찾을 수 없습니다: " + userId));
    }

    public Emotion getEmotionOrThrow(Integer emotionId) {
        return emotionRepository.findById(emotionId)
                .orElseThrow(() -> new IllegalArgumentException("선택한 감정을 찾을 수 없습니다: " + emotionId));
    }

    public UserProfile getActiveUserProfileOrThrow(Integer userId) {
        User user = getUserOrThrow(userId);

        return userProfileRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 프로필을 찾을 수 없습니다. User ID: " + userId));
    }
}
