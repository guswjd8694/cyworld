package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.common.util.JwtUtil;
import com.hyunjoying.cyworld.user.dto.request.*;
import com.hyunjoying.cyworld.user.entity.MiniHomepage;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.entity.UserProfile;
import com.hyunjoying.cyworld.user.repository.MinihomeRepository;
import com.hyunjoying.cyworld.user.repository.UserProfileRepository;
import com.hyunjoying.cyworld.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MinihomeRepository minihomeRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EntityFinder entityFinder;

    @Override
    @Transactional
    public void signUp(SignUpRequestDto requestDto) {
        if (userRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodePassword = passwordEncoder.encode(requestDto.getPassword());

        User newUser = new User();
        newUser.setLoginId(requestDto.getLoginId());
        newUser.setPassword(encodePassword);
        newUser.setName(requestDto.getName());
        newUser.setEmail(requestDto.getEmail());
        newUser.setBirth(formatBirth(requestDto.getBirth()));
        newUser.setPhone(formatPhone(requestDto.getPhone()));
        newUser.setGender(User.Gender.valueOf(requestDto.getGender()));

        User savedUser = userRepository.save(newUser);
        savedUser.setCreatedBy(savedUser.getId());

        MiniHomepage newMiniHomePage = new MiniHomepage();
        newMiniHomePage.setUser(savedUser);
        newMiniHomePage.setTitle(savedUser.getName() + "님의 미니홈피");
        newMiniHomePage.setCreatedBy(savedUser.getId());
        minihomeRepository.save(newMiniHomePage);

        UserProfile newUserProfile = new UserProfile();
        newUserProfile.setUser(savedUser);
        newUserProfile.setImageUrl("/대충이미지경로");
        newUserProfile.setBio("자기소개를 입력해주세요.");
        newUserProfile.setCreatedBy(savedUser.getId());
        userProfileRepository.save(newUserProfile);
    }

    private String formatBirth(String birth) {
        if (birth != null && birth.length() == 8) {
            return birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8);
        }
        return birth;
    }

    private String formatPhone(String phone) {
        if (phone != null && phone.length() == 11) {
            return phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7, 11);
        }
        return phone;
    }


    @Override
    public String login(LoginRequestDto requestDto) {
        User user = userRepository.findByLoginId(requestDto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        System.out.println("토큰 생성에 사용될 loginId: " + user.getLoginId());

        return jwtUtil.createToken(user.getLoginId());
    }

    @Override
    @Transactional
    public void updateUser(Integer userId, UpdateUserRequestDto requestDto) {
        User user = entityFinder.getUserOrThrow(userId);

        user.setEmail(requestDto.getEmail());
        user.setPhone(requestDto.getPhone());

        userRepository.save(user);
    }

    @Override
    public String findLoginId(FindLoginIdRequestDto requestDto) {
        User user = entityFinder.getUserNameAndEmailOrThrow(requestDto.getName(), requestDto.getEmail());

        return user.getLoginId();
    }

    @Override
    public void resetPassword(ResetPasswordRequestDto requestDto) {
        User user = entityFinder.getLoginIdAndEmailOrThrow(requestDto.getLoginId(), requestDto.getEmail());

        System.out.println("requestDto.getLoginId() : " + requestDto.getLoginId());
        System.out.println("requestDto.getEmail(): " + requestDto.getEmail());

        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
