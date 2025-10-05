package com.hyunjoying.cyworld.domain.board.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateBoardRequestDto {
    @Schema(example = "수정된 제목입니다.", description = "수정할 게시글 제목")
    private String title;

    @Schema(example = "<p>내용을 수정했어요!</p>", description = "수정할 게시글 내용")
    private String content;

    @Schema(description = "수정할 이미지 URL 목록")
    private List<String> imageUrls;

    @Schema(example = "흐림", description = "수정할 다이어리 날씨")
    private String weather;

    @Schema(example = "생각에 잠김", description = "수정할 다이어리 기분")
    private String mood;

    @Schema(example = "false", description = "수정할 공개 여부")
    private Boolean isPublic;
}