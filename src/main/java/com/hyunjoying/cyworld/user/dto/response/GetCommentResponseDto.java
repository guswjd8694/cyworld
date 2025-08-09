package com.hyunjoying.cyworld.user.dto.response;

import com.hyunjoying.cyworld.user.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
