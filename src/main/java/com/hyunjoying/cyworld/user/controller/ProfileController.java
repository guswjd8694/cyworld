package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.PostProfileResponseDto;
import com.hyunjoying.cyworld.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Operation(summary = "프로필 조회", description = "프로필 조회", tags = { "profile" })
    @ApiResponse(
        description = "프로필 조회 응답 값",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetProfileResponseDto.class))
    )
    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<GetProfileResponseDto> getProfile(@PathVariable Integer userId) {
        try {
            GetProfileResponseDto responseDto = profileService.getProfile(userId);

            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "프로필 수정", description = "프로필 수정", tags = { "profile" })
    @ApiResponse(
            description = "프로필 수정 요청",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostProfileResponseDto.class))
    )
    @PostMapping("/users/{userId}/profile")
    public PostProfileResponseDto postProfile(@PathVariable String userId){
        return new PostProfileResponseDto("success");
    }
}
