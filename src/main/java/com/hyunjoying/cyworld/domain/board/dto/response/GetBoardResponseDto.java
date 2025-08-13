package com.hyunjoying.cyworld.domain.board.dto.response;

import com.hyunjoying.cyworld.domain.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetBoardResponseDto {
    private final Integer boardId;
    private final Long boardNo;
    private final String writerName;
    private final String content;
    private final String type;
    private final boolean isPublic;
    private final LocalDateTime createdAt;
    private final String writerNickname;
    private final String writerLoginId;

    public GetBoardResponseDto(Board board, Long boardNo, String writerNickname) {
        this.boardId = board.getId();
        this.boardNo = boardNo;
        this.writerName = board.getUser().getName();
        this.content = board.getContent();
        this.type = board.getType();
        this.isPublic = board.isPublic();
        this.createdAt = board.getCreatedAt();
        this.writerNickname = writerNickname;
        this.writerLoginId = board.getUser().getLoginId();
    }
}