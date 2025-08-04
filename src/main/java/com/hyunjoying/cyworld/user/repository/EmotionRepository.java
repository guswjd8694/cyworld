package com.hyunjoying.cyworld.user.repository;

import com.hyunjoying.cyworld.user.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Integer> {
}
