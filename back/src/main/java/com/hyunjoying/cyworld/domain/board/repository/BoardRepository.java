package com.hyunjoying.cyworld.domain.board.repository;

import com.hyunjoying.cyworld.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    interface BoardCountByTypeDto {
        String getType();
        Long getTotalCount();
        Long getNewCount();
    }

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user"})
    Page<Board> findByMiniHomepageIdAndType(Integer miniHomepageId, String type, Pageable pageable);

    Page<Board> findByMiniHomepageIdAndTypeAndIsPublicTrue(Integer miniHomepageId, String type, Pageable pageable);


    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user"})
    List<Board> findAllByMiniHomepageIdAndTypeOrderByCreatedAtDesc(Integer miniHomepageId, String type);

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user"})
    List<Board> findByMiniHomepageIdAndTypeAndCreatedAtBetween(Integer miniHomepageId, String type, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user"})
    List<Board> findByMiniHomepageIdAndTypeAndIsPublicTrueAndCreatedAtBetween(Integer miniHomepageId, String type, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user", "images"})
    List<Board> findTop4ByMiniHomepageIdAndTypeInOrderByCreatedAtDesc(Integer miniHomepageId, List<String> types);

    @EntityGraph(attributePaths = {"user", "miniHomepage", "miniHomepage.user", "images"})
    List<Board> findTop4ByMiniHomepageIdAndTypeInAndIsPublicTrueOrderByCreatedAtDesc(Integer miniHomepageId, List<String> types);


    @Query("SELECT b.type AS type, COUNT(b.id) AS totalCount, " +
            "SUM(CASE WHEN b.createdAt > :recentDate THEN 1 ELSE 0 END) AS newCount " +
            "FROM Board b " +
            "WHERE b.miniHomepage.id = :miniHomepageId AND b.type IN :types " +
            "GROUP BY b.type")
    List<BoardCountByTypeDto> countBoardsByType(@Param("miniHomepageId") Integer miniHomepageId,
                                                @Param("types") List<String> types,
                                                @Param("recentDate") LocalDateTime recentDate);


    long countByMiniHomepageIdAndType(Integer miniHomepageId, String type);
    long countByMiniHomepageIdAndTypeAndCreatedAtAfter(Integer miniHomepageId, String type, LocalDateTime createdAt);
}
