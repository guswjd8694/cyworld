package com.hyunjoying.cyworld.user.service;


import com.hyunjoying.cyworld.user.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.user.entity.User;

public interface MinihomeService {
    GetMinihomeResponseDto getMinihome(Integer userId);
    void updateMinihome(Integer userId, UpdateMinihomeRequestDto requestDto);
    void recordVisitAndIncrementCounters(Integer userId, User visitor);
}

