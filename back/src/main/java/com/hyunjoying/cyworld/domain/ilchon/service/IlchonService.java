package com.hyunjoying.cyworld.domain.ilchon.service;

import com.hyunjoying.cyworld.domain.ilchon.dto.request.UpdateIlchonNicknameRequestDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonRelationshipResponseDto;

import java.util.List;

public interface IlchonService {

    void breakIlchon(Integer currentUserId, Integer targetUserId);

    List<GetIlchonResponseDto> getIlchons(Integer userId);

    void updateIlchonNickname(Integer currentUserId, UpdateIlchonNicknameRequestDto requestDto);

    GetIlchonRelationshipResponseDto getRelationship(Integer currentUserId, Integer targetUserId);

     Integer countMutualIlchons(Integer currentUserId, Integer targetUserId);
}