package com.hyunjoying.cyworld.domain.bgm.service;

import com.hyunjoying.cyworld.domain.bgm.dto.response.GetPlaylistResponseDto;

import java.util.List;

public interface BgmService {
    List<GetPlaylistResponseDto> getBgmByUserId(Integer userId);
}
