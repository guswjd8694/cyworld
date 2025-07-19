package com.hyunjoying.cyworld.user.dto.response.schma;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProfileHistory {
    private Integer profileId;
    private String profileImage;
    private String bio;
    private Instant createdAt;

    public ProfileHistory(Integer profileId, String profileImage, String bio, Instant createdAt) {
        this.profileId = profileId;
        this.profileImage = profileImage;
        this.bio = bio;
        this.createdAt = createdAt;
    }
}
