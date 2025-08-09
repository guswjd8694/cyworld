package com.hyunjoying.cyworld.user.repository;

import com.hyunjoying.cyworld.user.entity.Board;
import com.hyunjoying.cyworld.user.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByBoardOrderByCreatedAtAsc(Board board);
}
