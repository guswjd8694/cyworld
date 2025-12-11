package com.hyunjoying.cyworld.domain.auth.service;

import com.hyunjoying.cyworld.domain.auth.dto.request.CheckLoginIdRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.LoginRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.ResetPasswordRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.FindLoginIdRequestDto;

import java.util.Map;

public interface AuthService {
    String login(LoginRequestDto requestDto);
    String findLoginId(FindLoginIdRequestDto requestDto);
    void resetPassword(ResetPasswordRequestDto requestDto);
    Map<String, Boolean> checkLoginId(String loginId);
}
