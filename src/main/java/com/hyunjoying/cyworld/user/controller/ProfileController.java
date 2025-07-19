package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.request.GetProfileRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetEmotionResponseDto;
import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.schma.ProfileHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Operation(summary = "프로필 조회", description = "프로필 조회", tags = { "profile" })
    @ApiResponse(
        description = "프로필 조회 응답 값",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetProfileResponseDto.class))
    )
    @GetMapping("/users/{userId}/profile")
    public GetProfileResponseDto getProfile(@PathVariable String userId, @ModelAttribute GetProfileRequestDto getProfileRequestDto) {
        Instant now = Instant.now();
        String birthDateString = "1999-01-28";
        LocalDate birthDate = LocalDate.parse(birthDateString);
        ProfileHistory profileHistory = new ProfileHistory(
                2,
                ".jpg",
                "답답하면<br>너희들이<br>가서뛰던지~",
                now
        );

        List<ProfileHistory> profileHistoryList = List.of(profileHistory);

        return new GetProfileResponseDto(profileHistoryList, "김현정", "Female", birthDate, "ajdrns8694@gmail.com");
    }
}
