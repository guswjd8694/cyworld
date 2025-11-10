package com.hyunjoying.cyworld.domain.ilchon.service;

import com.hyunjoying.cyworld.domain.ilchon.dto.request.RequestIlchonDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.UpdateIlchonRequestStatusDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonRequestResponseDto;

import java.util.List;

public interface IlchonRequestService {

    void createIlchonRequest(Integer currentUserId, RequestIlchonDto requestDto);

    void updateIlchonRequestStatus(Integer currentUserId, Integer ilchonRequestId, UpdateIlchonRequestStatusDto requestDto);

    void cancelIlchonRequest(Integer currentUserId, Integer ilchonRequestId);

    List<GetIlchonRequestResponseDto> getReceivedIlchonRequests(Integer currentUserId, String status);

    List<GetIlchonRequestResponseDto> getSentIlchonRequests(Integer currentUserId, String status);
}