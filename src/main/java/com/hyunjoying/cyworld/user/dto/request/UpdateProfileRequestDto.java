package com.hyunjoying.cyworld.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequestDto {
    private String profileImageUrl;
    private String bio;
}
