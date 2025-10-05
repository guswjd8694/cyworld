package com.hyunjoying.cyworld.domain.board.repository;

import com.hyunjoying.cyworld.domain.board.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, Integer> {
}