package com.hyunjoying.cyworld.user.repository;

import com.hyunjoying.cyworld.user.entity.MiniHomepage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MinihomeRepository extends JpaRepository<MiniHomepage, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE MiniHomepage m SET m.todayVisits = 0")

    void resetAllTodayVisits();
}
