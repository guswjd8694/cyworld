package com.hyunjoying.cyworld.user.service;

import org.springframework.stereotype.Service;

@Service
public interface EmotionService {
    public String getEmotion(String emotion);
    public String updateEmotion(String emotion);
}
