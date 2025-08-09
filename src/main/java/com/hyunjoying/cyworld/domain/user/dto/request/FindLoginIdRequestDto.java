package com.hyunjoying.cyworld.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindLoginIdRequestDto {
    private String name;
    private String email;
}
