package com.hyunjoying.cyworld.domain.auth.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.common.util.JwtUtil;
import com.hyunjoying.cyworld.domain.auth.dto.request.CheckLoginIdRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.LoginRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.ResetPasswordRequestDto;
import com.hyunjoying.cyworld.domain.auth.dto.request.FindLoginIdRequestDto;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EntityFinder entityFinder;

    @Override
    public String login(LoginRequestDto requestDto) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(requestDto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        log.info("토큰 생성에 사용될 loginId: {}", user.getLoginId());
        return jwtUtil.createToken(user.getLoginId(), user.getId(), user.getName());
    }


    @Override
    public String findLoginId(FindLoginIdRequestDto requestDto) {
        User user = entityFinder.getUserNameAndEmailOrThrow(requestDto.getName(), requestDto.getEmail());
        return user.getLoginId();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto requestDto) {
        User user = entityFinder.getLoginIdAndEmailOrThrow(requestDto.getLoginId(), requestDto.getEmail());
        log.info("비밀번호 재설정 요청. loginId: {}, email: {}", requestDto.getLoginId(), requestDto.getEmail());

        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());

        user.updatePassword(encodedPassword);
    }


    @Override
    @Transactional(readOnly = true)
    public Map<String, Boolean> checkLoginId(String loginId) {
        boolean exists = userRepository.existsByLoginId(loginId);
        boolean isAvailable = !exists;

        return Map.of("isAvailable", isAvailable);
    }
}
