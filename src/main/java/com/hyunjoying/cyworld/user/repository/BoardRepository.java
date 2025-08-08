package com.hyunjoying.cyworld.user.repository;

import com.hyunjoying.cyworld.user.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findByMiniHomepageIdAndType(Integer miniHompageId, String type, Pageable pageable);
}
