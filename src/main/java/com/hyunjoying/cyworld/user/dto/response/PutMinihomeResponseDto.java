package com.hyunjoying.cyworld.user.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutMinihomeResponseDto {
    @Schema(example = "success", description = "미니홈피 수정 성공 여부 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    public PutMinihomeResponseDto(String status) {
        this.status = status;
    }
}
