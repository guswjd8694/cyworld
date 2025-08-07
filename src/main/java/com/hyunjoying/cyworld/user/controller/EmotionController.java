package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateEmotionRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetEmotionResponseDto;
import com.hyunjoying.cyworld.user.service.EmotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/emotion")
public class EmotionController {
    @Autowired
    private EmotionService emotionService;


    @Operation(summary = "감정 조회", description = "감정 조회", tags = { "emotion" })
    @ApiResponse(
        description = "감정 조회 응답 값",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetEmotionResponseDto.class))
    )
    @GetMapping("/users/{userId}/emotions")
    public ResponseEntity<GetEmotionResponseDto> getEmotion(@PathVariable Integer userId) {
        GetEmotionResponseDto responseDto = emotionService.getEmotion(userId);

        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "감정 수정", description = "감정 수정", tags = { "emotion" })
    @ApiResponse(
            description = "감정 수정 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/users/{userId}/emotions")
    public ResponseEntity<SuccessResponseDto> putEmotion(
            @PathVariable Integer userId,
            @RequestBody UpdateEmotionRequestDto requestDto
    ){
        emotionService.updateEmotion(userId, requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("오늘의 감정이 성공적으로 수정되었습니다."));
    }
}
