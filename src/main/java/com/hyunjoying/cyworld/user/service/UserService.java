package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.LoginRequestDto;
import com.hyunjoying.cyworld.user.dto.request.SignUpRequestDto;

public interface UserService {
    void signUp(SignUpRequestDto requestDto);
    String login(LoginRequestDto requestDto);
}
