package com.hyunjoying.cyworld.domain.user.service;

import com.hyunjoying.cyworld.domain.auth.dto.request.CheckLoginIdRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.LoginRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.ResetPasswordRequestDto;
import com.hyunjoying.cyworld.domain.user.dto.request.*;
import com.hyunjoying.cyworld.domain.user.dto.response.GetUserResponseDto;
import com.hyunjoying.cyworld.domain.user.entity.User;

import java.util.Map;

public interface UserService {
    void signUp(SignUpRequestDto requestDto);
    void updateUser(Integer userId, User currentUser, UpdateUserRequestDto requestDto);
    GetUserResponseDto getUserById(Integer userId);
    GetUserResponseDto getUserByLoginId(String loginId);
    void withdrawUser(Integer userId, User currentUser);
    GetUserResponseDto getRandomUserForVisit(Integer currentUserId);
    GetUserResponseDto getRandomUserForRecommendation(Integer currentUserId);
}
