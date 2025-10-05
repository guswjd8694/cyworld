package com.hyunjoying.cyworld.domain.profile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequestDto {

    @Schema(example = "/img/new_profile_pic.png", description = "새로운 프로필 이미지 URL")
    private String imageUrl;

    @Schema(example = "나 프로필 업뎃함~", description = "새로운 프로필 한 줄 메시지")
    private String bio;
}