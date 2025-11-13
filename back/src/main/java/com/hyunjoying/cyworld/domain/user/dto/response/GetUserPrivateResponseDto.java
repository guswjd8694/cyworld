package com.hyunjoying.cyworld.domain.user.dto.response;

import com.hyunjoying.cyworld.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Schema(description = "사용자 정보 응답 DTO")
public class GetUserPrivateResponseDto {
    @Schema(example = "1", description = "사용자 고유 식별자(PK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer id;

    @Schema(example = "user1", description = "로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String loginId;

    @Schema(example = "김현정", description = "이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String name;

    @Schema(example = "khj@email.com", description = "이메일")
    private String email;

    @Schema(example = "010-1234-5678", description = "전화번호")
    private String phone;

    @Setter
    @Schema(example = "/img/profile_pic.png", description = "현재 프로필 이미지 URL")
    private String imageUrl;

    @Setter
    @Schema(example = "5", description = "함께 아는 일촌 수")
    private Integer mutualIlchons;

    public GetUserPrivateResponseDto(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}