package com.hyunjoying.cyworld.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateBoardRequestDto {
    private Integer minihomepageOwnerId;
    private String content;
    private String type;
    private boolean isPublic;
}
