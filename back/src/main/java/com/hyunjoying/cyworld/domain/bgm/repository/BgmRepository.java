package com.hyunjoying.cyworld.domain.bgm.repository;

import com.hyunjoying.cyworld.domain.bgm.entity.Bgm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BgmRepository extends JpaRepository<Bgm, Integer> {
    @EntityGraph(attributePaths = {"user"})
    List<Bgm> findAllByUserIdAndIsDeletedFalse(Integer userId);

    List<Bgm> findAllByUserIdOrderByPlayOrderAsc(Integer userId);
}

