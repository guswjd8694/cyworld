package com.hyunjoying.cyworld.domain.profile.dto.response.schema;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProfileHistoryDto {
    private Integer profileId;
    private String profileImage;
    private String bio;
    private Instant createdAt;

    public ProfileHistoryDto(Integer profileId, String profileImage, String bio, Instant createdAt) {
        this.profileId = profileId;
        this.profileImage = profileImage;
        this.bio = bio;
        this.createdAt = createdAt;
    }
}
