package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.schma.ProfileHistoryDto;

import java.util.Date;
import java.util.List;


public interface ProfileService {
    GetProfileResponseDto getProfile(Integer userId);
    void updateProfile(Integer userId, UpdateProfileRequestDto requestDto);
}
