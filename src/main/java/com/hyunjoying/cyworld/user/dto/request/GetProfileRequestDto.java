package com.hyunjoying.cyworld.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProfileRequestDto {
    private Integer limit;

    public GetProfileRequestDto(Integer limit) {
        this.limit = limit;
    }
}