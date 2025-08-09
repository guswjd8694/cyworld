package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.user.dto.request.LoginRequestDto;
import com.hyunjoying.cyworld.user.dto.request.SignUpRequestDto;
import com.hyunjoying.cyworld.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
