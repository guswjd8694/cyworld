package com.hyunjoying.cyworld.domain.minihomepage.service;

import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;

public interface MinihomeService {
    GetMinihomeResponseDto getMinihomeInfo(Integer userId);
    void updateMinihomeTitle(Integer userId, UpdateMinihomeRequestDto requestDto);
    GetMinihomeResponseDto recordAndIncrementVisit(Integer ownerUserId, Integer visitorId);
}

