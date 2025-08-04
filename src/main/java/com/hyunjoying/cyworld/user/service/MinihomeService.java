package com.hyunjoying.cyworld.user.service;


import com.hyunjoying.cyworld.user.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.user.entity.User;

public interface MinihomeService {
    Integer getTodayVisit(Integer miniHompageId);
    Integer getTotalVisit(Integer miniHompageId);
    String getTitle(Integer miniHompageId);
    String getUrl(Integer miniHompageId);

    void recordVisitAndIncrementCounters(Integer miniHomepageId, User visitor);
    void updateMinihomeTitle(Integer miniHomepageId, UpdateMinihomeRequestDto requestDto);
}

