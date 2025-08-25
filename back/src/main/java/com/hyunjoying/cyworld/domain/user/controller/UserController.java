package com.hyunjoying.cyworld.domain.user.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import com.hyunjoying.cyworld.domain.user.dto.request.*;
import com.hyunjoying.cyworld.domain.user.dto.response.GetUserResponseDto;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EntityFinder entityFinder;


    @Operation(summary = "회원가입", description = "새로운 사용자를 생성합니다.", tags = { "user" })
    @ApiResponse(
            description = "회원가입 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/signup")
    public ResponseEntity<SuccessResponseDto> signUp(@RequestBody SignUpRequestDto requestDto) {
        userService.signUp(requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("회원가입이 성공적으로 완료되었습니다."));
    }


    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.", tags = { "user" })
    @ApiResponse(
            description = "로그인 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        String token = userService.login(requestDto);

        System.out.println("생성된 JWT 토큰: " + token);

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


    @Operation(summary = "개인정보 수정", description = "로그인한 사용자의 개인정보(이메일, 전화번호)를 수정합니다.", tags = { "user" })
    @ApiResponse(
            description = "개인정보 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/mypage")
    public ResponseEntity<SuccessResponseDto> updateUser(
            @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        User currentUser = userDetails.getUser();

        userService.updateUser(currentUser.getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("개인정보가 성공적으로 수정되었습니다."));
    }


    @Operation(summary = "아이디 찾기", description = "이름과 이메일로 아이디를 찾습니다.", tags = { "user" })
    @ApiResponse(
            description = "아이디 찾기 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    )
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, String>> findLoginId(@RequestBody FindLoginIdRequestDto requestDto) {
        String loginId = userService.findLoginId(requestDto);

        Map<String, String> response = new HashMap<>();
        response.put("loginId", loginId);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "비밀번호 재설정", description = "아이디와 이메일로 비밀번호를 재설정합니다.", tags = { "user" })
    @ApiResponse(
            description = "비밀번호 재설정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/reset-password")
    public ResponseEntity<SuccessResponseDto> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
        userService.resetPassword(requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("비밀번호가 성공적으로 재설정되었습니다."));
    }


    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자의 계정을 비활성화(소프트 딜리트)합니다.", tags = { "user" })
    @DeleteMapping("/{userId}")
    public ResponseEntity<SuccessResponseDto> withdrawUser(@PathVariable Integer userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.withdrawUser(userId);
        return ResponseEntity.ok(new SuccessResponseDto("회원 탈퇴가 성공적으로 처리되었습니다."));
    }



    @Operation(summary = "로그인 ID 개별 사용자 정보 조회", description = "로그인 ID로 특정 사용자의 정보를 조회합니다.", tags = { "user" })
    @GetMapping("/by-login-id/{loginId}")
    public ResponseEntity<GetUserResponseDto> getUserByLoginId(@PathVariable String loginId) {
        GetUserResponseDto userDto = userService.getUserByLoginId(loginId);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "유저 ID 개별 사용자 정보 조회", description = "ID로 특정 사용자의 정보를 조회합니다.", tags = { "user" })
    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponseDto> getUserById(@PathVariable Integer userId) {
        User user = entityFinder.getUserOrThrow(userId);
        GetUserResponseDto responseDto = new GetUserResponseDto(user);
        return ResponseEntity.ok(responseDto);
    }
}
