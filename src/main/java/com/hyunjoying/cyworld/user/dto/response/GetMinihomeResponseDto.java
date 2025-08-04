package com.hyunjoying.cyworld.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMinihomeResponseDto {
    @Schema(example = "64", description = "미니홈피 투데이 방문자 수", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer todayVisit;

    @Schema(example = "52579", description = "미니홈피 토탈 방문자 수", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalVisit;

    @Schema(example = "현정이의 미니홈피", description = "미니홈피 타이틀", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(example = "cyworld.com/hyunjoying", description = "미니홈피 주소", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    public GetMinihomeResponseDto(Integer todayVisit, Integer totalVisit, String title, String url) {
        this.todayVisit = todayVisit;
        this.totalVisit = totalVisit;
        this.title = title;
        this.url = url;
    }
}
