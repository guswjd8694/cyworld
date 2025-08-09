package com.hyunjoying.cyworld.domain.board.dto.response;

import com.hyunjoying.cyworld.domain.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetBoardResponseDto {
    private Integer boardId;
    private String writer;
    private String content;
    private String type;
    private boolean isPublic;
    private LocalDateTime createdAt;

    public GetBoardResponseDto(Board board) {
        this.boardId = board.getId();
        this.writer = board.getUser().getName();
        this.content = board.getContent();
        this.type = board.getType();
        this.isPublic = board.isPublic();
        this.createdAt = board.getCreatedAt();
    }
}
