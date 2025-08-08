package com.hyunjoying.cyworld.user.dto.response;

import com.hyunjoying.cyworld.user.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
