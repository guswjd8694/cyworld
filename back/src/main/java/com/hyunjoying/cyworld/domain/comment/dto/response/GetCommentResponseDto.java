package com.hyunjoying.cyworld.domain.comment.dto.response;

import com.hyunjoying.cyworld.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetCommentResponseDto {
    private final Integer commentId;
    private final String writer;
    private final String content;
    private final LocalDateTime createAt;

    public GetCommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.writer = comment.getUser().getName();
        this.content = comment.getContent();
        this.createAt = comment.getCreatedAt();
    }
}
