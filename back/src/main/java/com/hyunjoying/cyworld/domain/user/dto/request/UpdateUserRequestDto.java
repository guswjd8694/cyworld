package com.hyunjoying.cyworld.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequestDto {
    @Schema(example = "new_email@email.com", description = "새로운 이메일 주소")
    private String email;

    @Schema(example = "홍삼동", description = "새로운 이름")
    private String name;

    @Schema(example = "20020128", description = "새로운 생년월일")
    private String birth;

    @Schema(example = "Male", description = "새로운 성별 (Male/Female)")
    private String gender;

    @Schema(example = "01012345678", description = "새로운 전화번호")
    private String phone;
}