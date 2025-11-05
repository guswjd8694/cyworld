package com.hyunjoying.cyworld.domain.board.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBoardPrivacyDto {
    @Schema(example = "true", description = "공개 여부 설정 (기본값: true)")
    @JsonProperty("isPublic")
    private Boolean publicSetting;
}
