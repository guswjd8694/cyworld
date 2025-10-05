package com.hyunjoying.cyworld.domain.profile.dto.response;


import com.hyunjoying.cyworld.domain.profile.dto.response.schema.ProfileHistoryDto;
import com.hyunjoying.cyworld.domain.profile.entity.UserProfile;
import com.hyunjoying.cyworld.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class GetProfileResponseDto {
    private List<ProfileHistoryDto> history;

    @Schema(example = "김현정", description = "사용자 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(example = "Female", description = "사용자 성별", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gender;

    @Schema(example = "1999-01-28", description = "사용자 생년월일", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate birthday;

    @Schema(example = "ajdrns8694@gmail.com", description = "사용자 이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(example = "/img/image_01.jpg", description = "프로필 이미지 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageUrl;

    @Schema(example = "답답하면<br>너희들이<br>가서뛰던지~", description = "프로필 한 줄 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bio;

    @Schema(example = "💓 사랑", description = "오늘의 감정", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emotion;

    public GetProfileResponseDto(User user, UserProfile activeProfile, List<ProfileHistoryDto> history) {
        this.history = history;
        this.name = user.getName();
        this.gender = user.getGender().toString();
        this.birthday = user.getBirth() != null ? LocalDate.parse(user.getBirth()) : null;
        this.email = user.getEmail();
        this.imageUrl = activeProfile.getImageUrl();
        this.bio = activeProfile.getBio();
        this.emotion = user.getEmotion() != null ? user.getEmotion().getName() : "\uD83C\uDF37 행복";
    }
}

