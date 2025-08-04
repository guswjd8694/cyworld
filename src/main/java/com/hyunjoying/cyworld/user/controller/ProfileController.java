package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.request.GetProfileRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.PostProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.schma.ProfileHistoryDto;
import com.hyunjoying.cyworld.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


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
    public GetProfileResponseDto getProfile(@PathVariable Long userId, @ModelAttribute GetProfileRequestDto getProfileRequestDto) {
        Instant now = Instant.now();
        String birthDateString = "1999-01-28";
        LocalDate birthDate = LocalDate.parse(birthDateString);
        ProfileHistoryDto profileHistory = new ProfileHistoryDto(
                2,
                ".jpg",
                "답답하면<br>너희들이<br>가서뛰던지~",
                now
        );

        List<ProfileHistoryDto> profileHistoryList = List.of(profileHistory);

        return new GetProfileResponseDto(profileHistoryList, profileService.getName(userId), profileService.getGender(userId), birthDate, profileService.getEmail(userId));
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
