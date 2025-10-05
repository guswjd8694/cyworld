package com.hyunjoying.cyworld.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyunjoying.cyworld.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetCommentResponseDto {

    @Schema(example = "101", description = "댓글의 고유 식별자(PK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer commentId;

    @Schema(example = "1", description = "댓글 작성자의 고유 식별자(PK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer writerId;

    @Schema(example = "김현정", description = "댓글 작성자의 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String writer;

    @Schema(example = "아싸 내가 1빠", description = "댓글 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String content;

    @Schema(example = "2025-10-05T21:43:00", description = "댓글 작성 시간", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createAt;

    public GetCommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.writerId = comment.getUser().getId();
        this.writer = comment.getUser().getName();
        this.content = comment.getContent();
        this.createAt = comment.getCreatedAt();
    }
}