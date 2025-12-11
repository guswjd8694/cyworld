package com.hyunjoying.cyworld.domain.ilchon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestIlchonDto {
    @Schema(example = "2", description = "일촌을 신청할 대상 사용자의 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetUserId;

    @Schema(example = "코딩천재우진", description = "내가 상대방에게 설정할 일촌명")
    private String nicknameForToUser;

    @Schema(example = "기욤현정짱짱", description = "상대방이 나에게 설정할 일촌명")
    private String nicknameForFromUser;

    @Schema(example = "우리 일촌해요! 잘 부탁드립니다~", description = "일촌 신청 메시지")
    private String requestMessage;
}