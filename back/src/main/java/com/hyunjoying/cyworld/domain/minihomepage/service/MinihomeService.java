package com.hyunjoying.cyworld.domain.minihomepage.service;


import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.domain.user.entity.User;

public interface MinihomeService {
    GetMinihomeResponseDto getMinihome(Integer userId);
    void updateMinihome(Integer userId, UpdateMinihomeRequestDto requestDto);
}

