package com.hyunjoying.cyworld.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentRequestDto {

    @Schema(example = "홈피가 예쁘네요", description = "작성할 댓글의 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
