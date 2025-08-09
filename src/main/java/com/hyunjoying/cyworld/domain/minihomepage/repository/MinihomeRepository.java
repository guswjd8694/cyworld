package com.hyunjoying.cyworld.domain.minihomepage.repository;

import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MinihomeRepository extends JpaRepository<MiniHomepage, Integer> {

    Optional<MiniHomepage> findByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE MiniHomepage m SET m.todayVisits = 0")
    void resetAllTodayVisits();
}
