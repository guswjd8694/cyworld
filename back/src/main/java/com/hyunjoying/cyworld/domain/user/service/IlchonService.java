package com.hyunjoying.cyworld.domain.user.service;

import com.hyunjoying.cyworld.domain.user.dto.request.RequestIlchonDto;
import com.hyunjoying.cyworld.domain.user.dto.response.GetIlchonResponseDto;

import java.util.List;

public interface IlchonService {
    void requestIlchon(Integer currentUserId, RequestIlchonDto requestDto);
    void acceptIlchon(Integer currentUserId, Integer ilchoneRequestId);
    void rejectIlchon(Integer currentUserId, Integer targetUserId);
    void cancelIlchonRequest(Integer currentUserId, Integer ilchonRequestId);
    void breakIlchon(Integer currentUserId, Integer targetUserId);
    List<GetIlchonResponseDto> getIlchon(Integer userId);
    List<GetIlchonResponseDto> getReceivedIlchonRequests(Integer currentUserId);
    List<GetIlchonResponseDto> getSentIlchonRequests(Integer currentUserId);
    String getIlchonStatus(Integer currentUserId, Integer targetUserId);
}
