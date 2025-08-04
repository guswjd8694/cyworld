package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.user.dto.response.PostProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.PutMinihomeResponseDto;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.service.MinihomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/mini-hompage")
public class MinihomeController {

    @Autowired
    private MinihomeService minihomeService;

    @Operation(summary = "미니홈피 조회", description = "미니홈피 조회", tags = { "minihome" })
    @ApiResponse(
            description = "미니홈피 조회 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostProfileResponseDto.class))
    )
    @GetMapping("/{miniHomepageId}")
    public ResponseEntity<GetMinihomeResponseDto> GetMinihomeInfo(
            @PathVariable Integer miniHomepageId,
            @AuthenticationPrincipal User visitor
    ){
        try {
            minihomeService.recordVisitAndIncrementCounters(miniHomepageId, visitor);

            GetMinihomeResponseDto responseDto = new GetMinihomeResponseDto(
                    minihomeService.getTodayVisit(miniHomepageId),
                    minihomeService.getTotalVisit(miniHomepageId),
                    minihomeService.getTitle(miniHomepageId),
                    minihomeService.getUrl(miniHomepageId)
            );
            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "미니홈피 수정", description = "미니홈피 수정", tags = { "minihome" })
    @ApiResponse(
            description = "미니홈피 수정 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostProfileResponseDto.class))
    )
    @PostMapping("/{miniHomepageId}")
    public ResponseEntity<PutMinihomeResponseDto> putMinihome(
            @PathVariable Integer miniHomepageId,
            @RequestBody UpdateMinihomeRequestDto requestDto
    ){
        try {
            minihomeService.updateMinihomeTitle(miniHomepageId, requestDto);

            return ResponseEntity.ok(new PutMinihomeResponseDto("미니홈피가 성공적으로 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
