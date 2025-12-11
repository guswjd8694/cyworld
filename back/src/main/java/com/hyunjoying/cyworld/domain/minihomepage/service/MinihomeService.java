package com.hyunjoying.cyworld.domain.minihomepage.service;

import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface MinihomeService {
    GetMinihomeResponseDto getMinihomeInfoByLoginId(String loginId);
    void updateMinihomeTitle(String loginId, UpdateMinihomeRequestDto requestDto);
    GetMinihomeResponseDto recordAndIncrementVisit(String ownerLoginId, Integer visitorId, HttpServletRequest request);
}

