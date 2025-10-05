package com.hyunjoying.cyworld.domain.user.dto.response;

import com.hyunjoying.cyworld.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "일촌 정보 응답 DTO")
public class GetIlchonResponseDto {
    @Schema(example = "15", description = "일촌 관계의 고유 식별자(PK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer ilchonRequestId;

    @Schema(example = "2", description = "일촌의 사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer friendId;

    @Schema(example = "홍우진", description = "일촌의 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String friendName;

    @Schema(example = "user2", description = "일촌의 로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String friendLoginId;

    @Schema(example = "코딩천재우진", description = "내가 친구에게 설정한 일촌명")
    private final String myNicknameForFriend;

    @Schema(example = "기욤현정짱짱", description = "친구가 나에게 설정한 일촌명")
    private final String friendNicknameForMe;

    @Schema(example = "우리 일촌해요!", description = "일촌 신청 시 보낸 메시지")
    private final String requestMessage;

    public GetIlchonResponseDto(Integer ilchonRequestId, User friend, String myNicknameForFriend, String friendNicknameForMe, String requestMessage) {
        this.ilchonRequestId = ilchonRequestId;
        this.friendId = friend.getId();
        this.friendName = friend.getName();
        this.friendLoginId = friend.getLoginId();
        this.myNicknameForFriend = myNicknameForFriend;
        this.friendNicknameForMe = friendNicknameForMe;
        this.requestMessage = requestMessage;
    }
}