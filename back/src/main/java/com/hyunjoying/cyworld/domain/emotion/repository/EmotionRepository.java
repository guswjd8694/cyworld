package com.hyunjoying.cyworld.domain.emotion.repository;

import com.hyunjoying.cyworld.domain.emotion.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Integer> {
}
