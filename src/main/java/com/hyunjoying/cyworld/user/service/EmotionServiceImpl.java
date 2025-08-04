package com.hyunjoying.cyworld.user.service;


import org.springframework.stereotype.Service;

@Service
public class EmotionServiceImpl implements EmotionService  {

    public String getEmotion(String emotion) {
        return "🌷 행복";
    }


    public String updateEmotion(String emotion) {
        return "💓 사랑";
    }
}
