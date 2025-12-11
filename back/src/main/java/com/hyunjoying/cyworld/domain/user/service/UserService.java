package com.hyunjoying.cyworld.domain.user.service;

import com.hyunjoying.cyworld.domain.user.dto.request.*;
import com.hyunjoying.cyworld.domain.user.dto.response.GetUserPrivateResponseDto;
import com.hyunjoying.cyworld.domain.user.dto.response.GetUserPublicResponseDto;
import com.hyunjoying.cyworld.domain.user.entity.User;

public interface UserService {
    void signUp(SignUpRequestDto requestDto);
    void updateUser(Integer userId, User currentUser, UpdateUserRequestDto requestDto);
    GetUserPrivateResponseDto getUserById(Integer userId);
    GetUserPublicResponseDto getUserByLoginId(String loginId);
    void withdrawUser(Integer userId, User currentUser);
    GetUserPublicResponseDto getRandomUserForVisit(Integer currentUserId);
    GetUserPublicResponseDto getRandomUserForRecommendation(Integer currentUserId);
}
