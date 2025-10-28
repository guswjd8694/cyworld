package com.hyunjoying.cyworld.domain.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateBoardPrivacyDto {

    private Boolean isPublic;
}
