package com.hyunjoying.cyworld.common.util;

import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.board.repository.BoardRepository;
import com.hyunjoying.cyworld.domain.emotion.entity.Emotion;
import com.hyunjoying.cyworld.domain.emotion.repository.EmotionRepository;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.minihomepage.repository.MinihomeRepository;
import com.hyunjoying.cyworld.domain.profile.entity.UserProfile;
import com.hyunjoying.cyworld.domain.profile.repository.UserProfileRepository;
import com.hyunjoying.cyworld.domain.user.entity.*;
import com.hyunjoying.cyworld.domain.user.repository.*;
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
    private final IlchonRepository ilchonRepository;


    public User getUserOrThrow(Integer currentUserId){
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + currentUserId));
    }

    public User getUserNameAndEmailOrThrow(String name, String email){
        return userRepository.findByNameAndEmailAndIsDeletedFalse(name, email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보가 없습니다."));
    }

    public User getLoginIdOrThrow(String loginId){
        return userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보가 없습니다."));
    }

    public User getLoginIdAndEmailOrThrow(String loginId, String email){
        return userRepository.findByLoginIdAndEmailAndIsDeletedFalse(loginId, email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보가 없습니다."));
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

    public Ilchon getIlchonOrThrow(Integer ilchonRequestId) {
        return ilchonRepository.findById(ilchonRequestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일촌 요청입니다."));
    }
}
