package com.hyunjoying.cyworld.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentRequestDto {
    @Schema(example = "잘 보고 갑니다~", description = "수정할 댓글 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
