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

    @Schema(example = "010-8765-4321", description = "새로운 전화번호")
    private String phone;
}