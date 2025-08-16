package com.hyunjoying.cyworld.domain.minihomepage.repository;

// 실질적으로 jpa를 사용할 때 레포지토리를 사용한다 25.08.02

import com.hyunjoying.cyworld.domain.minihomepage.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VisitsRepository extends JpaRepository<Visit, Integer> {
}
