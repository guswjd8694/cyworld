package com.hyunjoying.cyworld.domain.ilchon.service;

import com.hyunjoying.cyworld.domain.ilchon.dto.request.RequestIlchonDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.UpdateIlchonNicknameRequestDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonRequestResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonResponseDto;

import java.util.List;

public interface IlchonService {
    void requestIlchon(Integer currentUserId, RequestIlchonDto requestDto);

    void acceptIlchon(Integer currentUserId, Integer ilchonRequestId);

    void rejectIlchon(Integer currentUserId, Integer ilchonRequestId);

    void cancelIlchonRequest(Integer currentUserId, Integer ilchonRequestId);

    void breakIlchon(Integer currentUserId, Integer targetUserId);

    List<GetIlchonResponseDto> getIlchons(Integer userId);

    List<GetIlchonRequestResponseDto> getReceivedIlchonRequests(Integer currentUserId);

    List<GetIlchonResponseDto> getSentIlchonRequests(Integer currentUserId);

    String getIlchonStatus(Integer currentUserId, Integer targetUserId);

    Integer calculateRelationshipDegree(Integer currentUserId, Integer targetUserId);

    Integer countMutualIlchons(Integer currentUserId, Integer targetUserId);

    void updateIlchonNickname(Integer currentUserId, UpdateIlchonNicknameRequestDto requestDto);
}

