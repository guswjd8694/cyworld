package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.user.dto.request.LoginRequestDto;
import com.hyunjoying.cyworld.user.dto.request.SignUpRequestDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateUserRequestDto;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponseDto> signUp(@RequestBody SignUpRequestDto requestDto) {
        userService.signUp(requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("회원가입이 성공적으로 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        String token = userService.login(requestDto);

        System.out.println("생성된 JWT 토큰: " + token);

        return ResponseEntity.ok().header("Authorization", "Bearer " + token).body("로그인 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponseDto> logout() {
        return ResponseEntity.ok(new SuccessResponseDto("로그아웃 되었습니다."));
    }

    @PutMapping("/mypage")
    public ResponseEntity<SuccessResponseDto> updateUser(
            @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal User currentUser
    ){
        userService.updateUser(currentUser.getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("개인정보가 성공적으로 수정되었습니다."));
    }
}
