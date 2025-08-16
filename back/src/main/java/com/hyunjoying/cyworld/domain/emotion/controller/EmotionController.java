package com.hyunjoying.cyworld.domain.emotion.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.emotion.dto.GetEmotionListDto;
import com.hyunjoying.cyworld.domain.emotion.dto.request.UpdateEmotionRequestDto;
import com.hyunjoying.cyworld.domain.emotion.dto.response.GetEmotionResponseDto;
import com.hyunjoying.cyworld.domain.emotion.service.EmotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmotionController {
    private final EmotionService emotionService;

    @Operation(summary = "전체 감정 목록 조회", description = "선택 가능한 모든 감정의 목록을 조회합니다.", tags = {"emotion"})
    @ApiResponse(
            description = "전체 감정 목록 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetEmotionListDto.class))
    )
    @GetMapping("/emotions")
    public ResponseEntity<List<GetEmotionListDto>> getAllEmotions() {
        List<GetEmotionListDto> emotions = emotionService.getAllEmotions();
        return ResponseEntity.ok(emotions);
    }


    @Operation(summary = "특정 유저의 감정 조회", description = "특정 사용자의 현재 설정된 감정을 조회합니다.", tags = {"emotion"})
    @ApiResponse(
        description = "감정 목록 조회 성공",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetEmotionResponseDto.class))
    )
    @GetMapping("/users/{userId}/emotion")
    public ResponseEntity<GetEmotionResponseDto> getEmotion(@PathVariable Integer userId) {
        GetEmotionResponseDto responseDto = emotionService.getEmotion(userId);

        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "감정 수정", description = "로그인한 사용자의 오늘의 감정을 수정합니다.", tags = {"emotion"})
    @ApiResponse(
            description = "감정 수정 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/users/{userId}/emotion")
    public ResponseEntity<SuccessResponseDto> putEmotion(
            @PathVariable Integer userId,
            @RequestBody UpdateEmotionRequestDto requestDto
    ){
        emotionService.updateEmotion(userId, requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("오늘의 감정이 성공적으로 수정되었습니다."));
    }
}
