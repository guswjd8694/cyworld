package com.hyunjoying.cyworld.domain.minihomepage.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.domain.minihomepage.service.MinihomeService;
import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/mini-homepage")
public class MinihomeController {
    private final MinihomeService minihomeService;

    @Operation(summary = "미니홈피 정보 조회", description = "특정 사용자의 미니홈피 정보를 조회합니다.", tags = { "minihomepage" })
    @ApiResponse(
            description = "미니홈피 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetMinihomeResponseDto.class))
    )
    @GetMapping
    public ResponseEntity<GetMinihomeResponseDto> getMinihomeInfo(@PathVariable Integer userId) {
        GetMinihomeResponseDto responseDto = minihomeService.getMinihomeInfo(userId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "미니홈피 방문 및 조회수 증가", description = "미니홈피 방문 기록을 남기고 조회수를 1 증가시킵니다.", tags = { "minihomepage" })
    @ApiResponse(
            description = "미니홈피 방문 및 조회수 증가 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetMinihomeResponseDto.class))
    )
    @PostMapping("/visit")
    public ResponseEntity<GetMinihomeResponseDto> recordVisit(
            @PathVariable("userId") Integer ownerUserId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer visitorId = (userDetails != null) ? userDetails.getUser().getId() : null;

        GetMinihomeResponseDto updatedMinihome = minihomeService.recordAndIncrementVisit(ownerUserId, visitorId);

        return ResponseEntity.ok(updatedMinihome);
    }

    @Operation(summary = "미니홈피 제목 수정", description = "미니홈피의 제목을 수정합니다.", tags = { "minihomepage" })
    @ApiResponse(
            description = "미니홈피 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping
    public ResponseEntity<SuccessResponseDto> updateMinihomeTitle(
            @PathVariable Integer userId,
            @RequestBody UpdateMinihomeRequestDto requestDto
    ) {
        minihomeService.updateMinihomeTitle(userId, requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("미니홈피 타이틀이 성공적으로 수정되었습니다."));
    }
}