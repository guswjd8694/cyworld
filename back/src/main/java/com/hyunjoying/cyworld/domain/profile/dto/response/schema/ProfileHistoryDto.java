package com.hyunjoying.cyworld.domain.profile.dto.response.schema;

import com.hyunjoying.cyworld.domain.profile.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.Instant;
import java.time.ZoneId;

@Getter
public class ProfileHistoryDto {

    @Schema(example = "5", description = "프로필 히스토리의 고유 식별자(PK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer profileId;

    @Schema(example = "/img/old_profile_pic.jpg", description = "과거 프로필 이미지 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String imageUrl;

    @Schema(example = "옛날 자기소개입니다.", description = "과거 프로필 한 줄 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String bio;

    @Schema(example = "2025-09-01T10:00:00Z", description = "프로필 생성 시간 (UTC)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Instant createdAt;

    public ProfileHistoryDto(UserProfile profile) {
        this.profileId = profile.getId();
        this.imageUrl = profile.getImageUrl();
        this.bio = profile.getBio();
        this.createdAt = profile.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
    }
}