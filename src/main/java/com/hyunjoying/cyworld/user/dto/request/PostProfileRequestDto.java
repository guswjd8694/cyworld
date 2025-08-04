package com.hyunjoying.cyworld.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostProfileRequestDto {
    private String profileImage;
    private String bio;

    public PostProfileRequestDto(String profileImage, String bio) {
        this.profileImage = profileImage;
        this.bio = bio;
    }
}
