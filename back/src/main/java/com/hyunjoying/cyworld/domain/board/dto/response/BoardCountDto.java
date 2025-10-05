package com.hyunjoying.cyworld.domain.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class BoardCountDto {
    @Schema(example = "3", description = "새로 올라온 게시물 수", requiredMode = Schema.RequiredMode.REQUIRED)
    private final long newCount;

    @Schema(example = "152", description = "해당 게시판의 전체 게시물 수", requiredMode = Schema.RequiredMode.REQUIRED)
    private final long totalCount;

    public BoardCountDto(long newCount, long totalCount) {
        this.newCount = newCount;
        this.totalCount = totalCount;
    }
}