package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.response.GetEmotionResponseDto;
import com.hyunjoying.cyworld.user.dto.response.PutEmotionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/emotion")
public class EmotionController {
    @Operation(summary = "감정 조회", description = "감정 조회", tags = { "emotion" })
    @ApiResponse(
        description = "감정 조회 응답 값",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetEmotionResponseDto.class))
    )
    @GetMapping("/users/{userId}/emotions")
    public GetEmotionResponseDto getEmotion(@PathVariable String userId) {
        return new GetEmotionResponseDto("🌷 행복");
    }


    @Operation(summary = "감정 수정", description = "감정 수정", tags = { "emotion" })
    @ApiResponse(
            description = "감정 수정 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PutEmotionResponseDto.class))
    )
    @PutMapping("/users/{userId}/emotions")
    public PutEmotionResponseDto putEmotion(@PathVariable String userId){
        return new PutEmotionResponseDto("💓 사랑");
    }
}
