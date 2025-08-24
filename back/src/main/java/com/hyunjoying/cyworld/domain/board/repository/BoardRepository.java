package com.hyunjoying.cyworld.domain.board.repository;

import com.hyunjoying.cyworld.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.miniHomepage.id = :miniHomepageId AND b.type = :type AND b.isDeleted = false")
    Page<Board> findByMiniHomepageIdAndType(
            @Param("miniHomepageId") Integer miniHomepageId,
            @Param("type") String type,
            Pageable pageable
    );

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.miniHomepage.id = :miniHomepageId AND b.type = :type AND b.isDeleted = false ORDER BY b.createdAt DESC")
    List<Board> findAllByMiniHomepageIdAndType(
            @Param("miniHomepageId") Integer miniHomepageId,
            @Param("type") String type
    );

    Optional<Board> findByMiniHomepageIdAndTypeAndIsDeletedFalseAndCreatedAtBetween(
            Integer miniHompageId, String type, LocalDateTime startOfDay, LocalDateTime endOfDay
    );
}
