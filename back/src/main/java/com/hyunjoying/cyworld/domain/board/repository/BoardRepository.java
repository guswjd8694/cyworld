package com.hyunjoying.cyworld.domain.board.repository;

import com.hyunjoying.cyworld.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user"})
    Page<Board> findByMiniHomepageIdAndType(Integer miniHomepageId, String type, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user"})
    List<Board> findAllByMiniHomepageIdAndTypeOrderByCreatedAtDesc(Integer miniHomepageId, String type);

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user"})
    Optional<Board> findByMiniHomepageIdAndTypeAndCreatedAtBetween(
            Integer miniHomepageId, String type, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user", "images"})
    List<Board> findTop4ByMiniHomepageIdAndTypeInOrderByCreatedAtDesc(Integer miniHomepageId, List<String> types);

    long countByMiniHomepageIdAndType(Integer miniHomepageId, String type);

    long countByMiniHomepageIdAndTypeAndCreatedAtAfter(Integer miniHomepageId, String type, LocalDateTime createdAt);
}
