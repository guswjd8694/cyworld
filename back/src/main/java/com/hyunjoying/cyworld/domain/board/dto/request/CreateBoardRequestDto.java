package com.hyunjoying.cyworld.domain.board.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateBoardRequestDto {
    @Schema(hidden = true)
    private Integer minihomepageOwnerId;

    @Schema(example = "첫 글 작성! 클릭해봐", description = "생성할 게시글 제목")
    private String title;

    @Schema(example = "<p>새로운 글 내용입니다.</p>", description = "생성할 게시글 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "첨부할 이미지 URL 목록 (사진첩 타입에 필요)")
    private List<String> imageUrls;

    @Schema(example = "DIARY", description = "생성할 게시글 타입 (DIARY, PHOTO, GUESTBOOK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Schema(example = "바람", description = "다이어리 날씨")
    private String weather;

    @Schema(example = "상쾌함", description = "다이어리 기분")
    private String mood;

    @Schema(example = "true", description = "공개 여부 설정 (기본값: true)")
    @JsonProperty("isPublic")
    private Boolean publicSetting;
}