package com.hyunjoying.cyworld.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String email;
    private String phone;
}
