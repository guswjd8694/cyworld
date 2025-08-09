package com.hyunjoying.cyworld.domain.user.dto.response;

import com.hyunjoying.cyworld.domain.user.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import lombok.Getter;

@Getter
public class GetIlchonResponseDto {
    private final Integer ilchonRequestId;
    private final Integer friendId;
    private final String friendName;
    private final String myNicknameForFriend;
    private final String friendNicknameForMe;
    private final String requestMessage;

    public GetIlchonResponseDto(Integer ilchonRequestId, User friend, String myNicknameForFriend, String friendNicknameForMe, String requestMessage) {
        this.ilchonRequestId = ilchonRequestId;
        this.friendId = friend.getId();
        this.friendName = friend.getName();
        this.myNicknameForFriend = myNicknameForFriend;
        this.friendNicknameForMe = friendNicknameForMe;
        this.requestMessage = requestMessage;
    }
}
