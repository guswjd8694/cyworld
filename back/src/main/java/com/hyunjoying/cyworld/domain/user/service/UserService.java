package com.hyunjoying.cyworld.domain.user.service;

import com.hyunjoying.cyworld.domain.user.dto.request.*;
import com.hyunjoying.cyworld.domain.user.dto.response.GetUserResponseDto;

import java.util.Map;

public interface UserService {
    void signUp(SignUpRequestDto requestDto);
    String login(LoginRequestDto requestDto);
    void updateUser(Integer userId, UpdateUserRequestDto requestDto);
    String findLoginId(FindLoginIdRequestDto requestDto);
    void resetPassword(ResetPasswordRequestDto requestDto);
    GetUserResponseDto getUserByLoginId(String loginId);
    void withdrawUser(Integer userId);
    Map<String, Boolean> checkLoginId(CheckLoginIdRequestDto requestDto);
    GetUserResponseDto getRandomUserForVisit(Integer currentUserId);
    GetUserResponseDto getRandomUserForRecommendation(Integer currentUserId);
}
