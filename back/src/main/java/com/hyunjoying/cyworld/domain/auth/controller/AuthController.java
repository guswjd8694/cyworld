package com.hyunjoying.cyworld.domain.auth.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.CheckLoginIdRequestDto;
import com.hyunjoying.cyworld.domain.auth.service.AuthService;
import com.hyunjoying.cyworld.domain.auth.dto.request.FindLoginIdRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.LoginRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.ResetPasswordRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.", tags = { "user" })
    @ApiResponse(
            description = "로그인 성공. Authorization 헤더에 Bearer 토큰이 포함됩니다.",
            content = @Content(mediaType = "text/plain", schema = @Schema(example = "로그인 성공"))
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        String token = authService.login(requestDto);

        return ResponseEntity.ok().header("Authorization", "Bearer " + token).body("로그인 성공");
    }


    @Operation(summary = "로그아웃", description = "인증된 사용자의 JWT 토큰을 무효화합니다.", tags = { "user" })
    @ApiResponse(
            description = "로그아웃 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponseDto> logout() {
        return ResponseEntity.ok(new SuccessResponseDto("로그아웃 되었습니다."));
    }


    @Operation(summary = "비밀번호 재설정", description = "아이디와 이메일로 비밀번호를 재설정합니다.", tags = { "user" })
    @ApiResponse(
            description = "비밀번호 재설정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/reset-password")
    public ResponseEntity<SuccessResponseDto> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
        authService.resetPassword(requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("비밀번호가 성공적으로 재설정되었습니다."));
    }


    @Operation(summary = "아이디 찾기", description = "이름과 이메일로 아이디를 찾습니다.", tags = { "user" })
    @ApiResponse(
            description = "아이디 찾기 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class, example = "{\"loginId\": \"user1\"}"))
    )
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, String>> findLoginId(@RequestBody FindLoginIdRequestDto requestDto) {
        String loginId = authService.findLoginId(requestDto);

        Map<String, String> response = new HashMap<>();
        response.put("loginId", loginId);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "아이디 중복 체크", description = "회원가입 시 아이디 중복 여부를 확인합니다.", tags = { "user" })
    @ApiResponse(
            description = "아이디 중복 체크 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class, example = "{\"isAvailable\": true}"))
    )
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(
            @RequestParam String loginId
    ) {
        return ResponseEntity.ok(authService.checkLoginId(loginId));
    }
}