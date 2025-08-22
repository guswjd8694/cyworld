package com.hyunjoying.cyworld.domain.minihomepage.service;

import com.hyunjoying.cyworld.domain.user.entity.User;

public interface VisitService {
    void recordVisitAndIncrementCounters(Integer userId, User visitor);
}
