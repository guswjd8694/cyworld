package com.hyunjoying.cyworld.domain.ilchon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateIlchonNicknameRequestDto {
    @Schema(example = "2", description = "일촌명을 변경할 대상 사용자의 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetUserId;

    @Schema(example = "멋쟁이우진", description = "내가 상대방에게 설정할 새로운 일촌명")
    private String nicknameForToUser;

    @Schema(example = "절친현정이", description = "상대방이 나에게 설정할 새로운 일촌명")
    private String nicknameForFromUser;
}