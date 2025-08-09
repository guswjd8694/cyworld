package com.hyunjoying.cyworld.domain.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateBoardRequestDto {
    private String content;
    private Boolean isPublic;
}
