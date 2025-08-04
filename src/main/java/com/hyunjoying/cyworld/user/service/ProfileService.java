package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.schma.ProfileHistoryDto;

import java.util.Date;
import java.util.List;


public interface ProfileService {
    public String getName(Long userId);
    public String getGender(Long userId);
    public Date getBirthday(Long userId);
    public String getEmail(Long userId);
    public List<ProfileHistoryDto> getProfileHistory(Long userId);

    GetProfileResponseDto getProfile(Integer userId);
}
