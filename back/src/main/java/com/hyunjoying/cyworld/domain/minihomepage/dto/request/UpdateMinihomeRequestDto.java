package com.hyunjoying.cyworld.domain.minihomepage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMinihomeRequestDto {
    @Schema(example = "행복한 현정이네", description = "수정할 미니홈피의 새로운 제목", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
}
