package com.hyunjoying.cyworld.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestIlchonDto {
    private Integer targetUserId;
    private String userNickname;
    private String friendNickname;
    private String requestMessage;
}
