package com.hyunjoying.cyworld.domain.user.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.auth.dto.request.CheckLoginIdRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.LoginRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.ResetPasswordRequestDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.service.IlchonService;
import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import com.hyunjoying.cyworld.domain.user.dto.request.*;
import com.hyunjoying.cyworld.domain.user.dto.response.GetUserResponseDto;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final IlchonService ilchonService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 생성합니다.", tags = { "user" })
    @ApiResponse(
            description = "회원가입 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping
    public ResponseEntity<SuccessResponseDto> signUp(@RequestBody SignUpRequestDto requestDto) {
        userService.signUp(requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("회원가입이 성공적으로 완료되었습니다."));
    }


    @Operation(summary = "개인정보 수정", description = "로그인한 사용자의 개인정보(이메일, 전화번호)를 수정합니다.", tags = { "user" })
    @ApiResponse(
            description = "개인정보 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/{userId}")
    public ResponseEntity<SuccessResponseDto> updateUser(
            @PathVariable Integer userId,
            @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        User currentUser = userDetails.getUser();

        userService.updateUser(userId, currentUser, requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("개인정보가 성공적으로 수정되었습니다."));
    }


    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자의 계정을 비활성화(소프트 딜리트)합니다.", tags = { "user" })
    @ApiResponse(
            description = "회원 탈퇴 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<SuccessResponseDto> withdrawUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User currentUser = userDetails.getUser();

        userService.withdrawUser(userId, currentUser);
        return ResponseEntity.ok(new SuccessResponseDto("회원 탈퇴가 성공적으로 처리되었습니다."));
    }


    @Operation(summary = "유저 검색 (로그인 ID)", description = "로그인 ID로 특정 사용자의 정보를 조회합니다.", tags = { "user" })
    @ApiResponse(
            description = "사용자 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetUserResponseDto.class))
    )
    @GetMapping
    public ResponseEntity<GetUserResponseDto> getUserByLoginId(
            @RequestParam String loginId
    ) {
        GetUserResponseDto userDto = userService.getUserByLoginId(loginId);
        return ResponseEntity.ok(userDto);
    }


    @Operation(summary = "유저 ID 개별 사용자 정보 조회", description = "ID로 특정 사용자의 정보를 조회합니다.", tags = { "user" })
    @ApiResponse(
            description = "사용자 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetUserResponseDto.class))
    )
    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponseDto> getUserById(@PathVariable Integer userId) {
        GetUserResponseDto responseDto = userService.getUserById(userId);
        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "랜덤 사용자 조회 (파도타기)", description = "자기 자신을 제외한 모든 활성 사용자 중 랜덤으로 한 명의 정보를 조회합니다.", tags = { "user" })
    @ApiResponse(responseCode = "200", description = "랜덤 사용자 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetUserResponseDto.class)))
    @ApiResponse(responseCode = "204", description = "추천할 사용자가 없음")
    @GetMapping("/random-visit")
    public ResponseEntity<GetUserResponseDto> getRandomUserForVisit(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer currentUserId = userDetails.getUser().getId();

        GetUserResponseDto randomUserDto = userService.getRandomUserForVisit(currentUserId);

        if (randomUserDto == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(randomUserDto);
    }


    @Operation(summary = "랜덤 사용자 조회 (친구 추천용)", description = "일촌과 자기 자신을 제외한 활성 사용자 중 랜덤으로 한 명의 정보를 조회합니다.", tags = { "user" })
    @ApiResponse(responseCode = "200", description = "랜덤 사용자 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetUserResponseDto.class)))
    @ApiResponse(responseCode = "204", description = "추천할 사용자가 없음")
    @GetMapping("/random-recommendation")
    public ResponseEntity<GetUserResponseDto> getRandomUserForRecommendation(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer currentUserId = userDetails.getUser().getId();

        GetUserResponseDto randomUserDto = userService.getRandomUserForRecommendation(currentUserId);

        if (randomUserDto == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(randomUserDto);
    }


    @Operation(summary = "특정 사용자의 일촌 목록 조회", description = "특정 사용자의 일촌 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 목록 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetIlchonResponseDto.class)))
    )
    @GetMapping("/{userId}/ilchons")
    public ResponseEntity<List<GetIlchonResponseDto>> getIlchons(@PathVariable Integer userId) {
        List<GetIlchonResponseDto> ilchons = ilchonService.getIlchons(userId);
        return ResponseEntity.ok(ilchons);
    }
}