package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;


public interface ProfileService {
    GetProfileResponseDto getProfile(Integer userId);
    void updateProfile(Integer userId, UpdateProfileRequestDto requestDto);
}
