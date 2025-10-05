package com.hyunjoying.cyworld.domain.profile.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.profile.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.domain.profile.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.domain.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/profile")
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = "프로필 조회", description = "프로필 조회", tags = { "profile" })
    @ApiResponse(
        description = "프로필 조회 응답 값",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetProfileResponseDto.class))
    )
    @GetMapping
    public ResponseEntity<GetProfileResponseDto> getProfile(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "20", required = false) Integer limit
    ) {
        GetProfileResponseDto responseDto = profileService.getProfile(userId, limit);

        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "프로필 수정", description = "기존 프로필을 비활성화하고 새로운 프로필을 생성하여 히스토리를 남깁니다.", tags = { "profile" })
    @ApiResponse(
            description = "프로필 수정 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetProfileResponseDto.class))
    )
    @PostMapping
    public ResponseEntity<GetProfileResponseDto> postProfile(
            @PathVariable Integer userId,
            @RequestBody UpdateProfileRequestDto requestDto
    ){
        GetProfileResponseDto updatedProfile = profileService.updateProfile(userId, requestDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
