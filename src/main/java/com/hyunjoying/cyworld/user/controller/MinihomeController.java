package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.service.MinihomeService;
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


    @Operation(summary = "미니홈피 조회", description = "미니홈피 조회", tags = { "minihome" })
    @ApiResponse(
            description = "미니홈피 조회 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetProfileResponseDto.class))
    )
    @GetMapping
    public ResponseEntity<GetMinihomeResponseDto> getMinihomeInfo(
            @PathVariable Integer userId,
            @AuthenticationPrincipal User visitor
    ){
        minihomeService.recordVisitAndIncrementCounters(userId, visitor);

        GetMinihomeResponseDto responseDto = minihomeService.getMinihome(userId);
        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "미니홈피 수정", description = "미니홈피 수정", tags = { "minihome" })
    @ApiResponse(
            description = "미니홈피 수정 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping
    public ResponseEntity<SuccessResponseDto> putMinihome(
            @PathVariable Integer userId,
            @RequestBody UpdateMinihomeRequestDto requestDto
    ){
        minihomeService.updateMinihome(userId, requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("미니홈피 타이틀이 성공적으로 수정되었습니다."));
    }
}
