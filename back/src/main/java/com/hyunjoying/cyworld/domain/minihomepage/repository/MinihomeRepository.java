package com.hyunjoying.cyworld.domain.minihomepage.repository;

import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface MinihomeRepository extends JpaRepository<MiniHomepage, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"user"})
    Optional<MiniHomepage> findByUserId(Integer userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<MiniHomepage> findByUserIdAndIsDeletedFalse(Integer userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<MiniHomepage> findByUserLoginIdAndIsDeletedFalse(String loginId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE MiniHomepage m SET m.todayVisits = 0")
    void resetAllTodayVisits();
}
