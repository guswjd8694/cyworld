package com.hyunjoying.cyworld.domain.bgm.dto.response;

import com.hyunjoying.cyworld.domain.bgm.entity.Bgm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class GetPlaylistResponseDto {

    @Schema(example = "/music/freestyle_y.mp3", description = "음악 파일의 경로(URL)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String src;

    @Schema(example = "Y (Please Tell Me Why)", description = "음악 제목", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String title;

    @Schema(example = "프리스타일", description = "아티스트(가수) 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String artist;

    public GetPlaylistResponseDto(Bgm bgm) {
        this.src = bgm.getBgmUrl();
        this.title = bgm.getTitle();
        this.artist = bgm.getArtist();
    }
}