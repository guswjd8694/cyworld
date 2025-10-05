package com.hyunjoying.cyworld.domain.comment.repository;

import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findAllByBoardOrderByCreatedAtAsc(Board board);

    @EntityGraph(attributePaths = { "board", "board.miniHomepage", "board.miniHomepage.user" })
    Optional<Comment> findWithDetailsById(Integer id);
}
