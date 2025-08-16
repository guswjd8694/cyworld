package com.hyunjoying.cyworld.domain.profile.service;

import com.hyunjoying.cyworld.domain.profile.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.domain.profile.dto.response.GetProfileResponseDto;


public interface ProfileService {
    GetProfileResponseDto getProfile(Integer userId, Integer limit);
    void updateProfile(Integer userId, UpdateProfileRequestDto requestDto);
}
