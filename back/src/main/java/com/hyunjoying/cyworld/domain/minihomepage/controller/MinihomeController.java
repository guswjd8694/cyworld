package com.hyunjoying.cyworld.domain.minihomepage.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.domain.minihomepage.service.VisitService;
import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.minihomepage.service.MinihomeService;
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
    private final VisitService visitService;


    @Operation(summary = "미니홈피 조회", description = "미니홈피 조회", tags = { "minihome" })
    @ApiResponse(
            description = "미니홈피 조회 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetMinihomeResponseDto.class))
    )
    @GetMapping
    public ResponseEntity<GetMinihomeResponseDto> getMinihomeInfo(
            @PathVariable Integer userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){

        User visitor = (userDetails != null) ? userDetails.getUser() : null;
        boolean isOwner = (visitor != null && visitor.getId().equals(userId));

        if (!isOwner) {
            visitService.recordVisitAndIncrementCounters(userId, visitor);
        }

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
