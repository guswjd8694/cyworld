package com.hyunjoying.cyworld.domain.minihomepage.dto.response;

import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class GetMinihomeResponseDto {
    @Schema(example = "64", description = "미니홈피 투데이 방문자 수", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer todayVisit;

    @Schema(example = "52579", description = "미니홈피 토탈 방문자 수", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer totalVisit;

    @Schema(example = "현정이의 미니홈피", description = "미니홈피 타이틀", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String title;

    @Schema(example = "cyworld.com/hyunjoying", description = "미니홈피 주소", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String url;

    public GetMinihomeResponseDto(MiniHomepage miniHomepage) {
        this.todayVisit = miniHomepage.getTodayVisits();
        this.totalVisit = miniHomepage.getTotalVisits();
        this.title = miniHomepage.getTitle();
        this.url = "cyworld.com/" + miniHomepage.getUser().getLoginId();
    }
}
