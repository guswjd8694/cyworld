package com.hyunjoying.cyworld.domain.user.dto.response;

import com.hyunjoying.cyworld.domain.user.entity.Ilchon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Schema(description = "받은 일촌 신청 정보 응답 DTO")
public class GetIlchonRequestResponseDto {
    @Schema(example = "15", description = "일촌 신청의 고유 식별자(PK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer ilchonRequestId;

    @Schema(example = "홍우진", description = "신청한 사람의 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String requesterName;

    @Schema(example = "user2", description = "신청한 사람의 로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String requesterLoginId;

    @Schema(example = "김현정", description = "신청 받은 사람의 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String receiverName;

    @Schema(example = "기욤현정짱짱", description = "내가 신청자에게 설정할 일촌명")
    private final String myNicknameForRequester;

    @Schema(example = "코딩천재우진", description = "신청자가 나에게 설정할 일촌명")
    private final String requesterNicknameForMe;

    @Schema(example = "우리 일촌해요!", description = "일촌 신청 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String requestMessage;

    @Schema(example = "2025.09.01 22:30", description = "신청 시간", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String createdAt;

    public GetIlchonRequestResponseDto(Ilchon ilchonRequest) {
        this.ilchonRequestId = ilchonRequest.getId();
        this.requesterName = ilchonRequest.getUser().getName();
        this.requesterLoginId = ilchonRequest.getUser().getLoginId();
        this.receiverName = ilchonRequest.getFriend().getName();
        this.myNicknameForRequester = ilchonRequest.getFriendNickname();
        this.requesterNicknameForMe = ilchonRequest.getUserNickname();

        if (ilchonRequest.getRequestMessage() == null || ilchonRequest.getRequestMessage().trim().isEmpty()) {
            this.requestMessage = "일촌 신청합니다";
        } else {
            this.requestMessage = ilchonRequest.getRequestMessage();
        }

        if (ilchonRequest.getCreatedAt() != null) {
            this.createdAt = ilchonRequest.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        } else {
            this.createdAt = "";
        }
    }
}