package com.hyunjoying.cyworld.domain.minihomepage.repository;

// 실질적으로 jpa를 사용할 때 레포지토리를 사용한다 25.08.02

import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.minihomepage.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface VisitsRepository extends JpaRepository<Visit, Integer> {
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Visit v " +
            "WHERE v.miniHomepage = :miniHomepage " +
            "AND v.visitorIp = :visitorIp " +
            "AND v.createdAt >= :startOfDay " +
            "AND v.createdAt < :tomorrowStartOfDay")
    boolean existsByIpAndDateRange(
            @Param("miniHomepage") MiniHomepage miniHomepage,
            @Param("visitorIp") String visitorIp,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("tomorrowStartOfDay") LocalDateTime tomorrowStartOfDay
    );
}
