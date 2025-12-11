package com.hyunjoying.cyworld.domain.ilchon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "촌수 관계 조회 응답 DTO")
public class GetIlchonRelationshipResponseDto {

    @Schema(example = "1", description = "촌수 (0: 본인, 1: 일촌, 2~4: n촌, -1: 관계 없음)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer degree;

    @Schema(example = "코딩천재", description = "내가 상대방을 부르는 일촌명")
    private final String nicknameForToUser;

    @Schema(example = "기욤짱짱", description = "상대방이 나를 부르는 일촌명")
    private final String nicknameForFromUser;

}