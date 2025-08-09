package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.*;

public interface UserService {
    void signUp(SignUpRequestDto requestDto);
    String login(LoginRequestDto requestDto);
    void updateUser(Integer userId, UpdateUserRequestDto requestDto);
    String findLoginId(FindLoginIdRequestDto requestDto);
    void resetPassword(ResetPasswordRequestDto requestDto);
}
