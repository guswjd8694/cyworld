package com.hyunjoying.cyworld.domain.user.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.ilchon.service.IlchonService;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.minihomepage.repository.MinihomeRepository;
import com.hyunjoying.cyworld.domain.profile.entity.UserProfile;
import com.hyunjoying.cyworld.domain.profile.repository.UserProfileRepository;
import com.hyunjoying.cyworld.domain.user.dto.request.*;
import com.hyunjoying.cyworld.domain.user.dto.response.GetUserResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRepository;
import com.hyunjoying.cyworld.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MinihomeRepository minihomeRepository;
    private final UserProfileRepository userProfileRepository;
    private final IlchonRepository ilchonRepository;
    private final IlchonService ilchonService;
    private final PasswordEncoder passwordEncoder;
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

        User newUser = new User(
                requestDto.getLoginId(),
                encodePassword,
                requestDto.getName(),
                requestDto.getEmail(),
                formatPhone(requestDto.getPhone()),
                formatBirth(requestDto.getBirth()),
                User.Gender.valueOf(requestDto.getGender().toUpperCase())
        );
        User savedUser = userRepository.save(newUser);

        MiniHomepage newMiniHomePage = new MiniHomepage(savedUser);
        newMiniHomePage.setCreatedBy(savedUser.getId());
        minihomeRepository.save(newMiniHomePage);

        UserProfile newUserProfile = new UserProfile(savedUser, requestDto.getGender());
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
    @Transactional
    public void updateUser(Integer userId, User currentUser, UpdateUserRequestDto requestDto) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 정보만 수정할 수 있습니다.");
        }

        User user = entityFinder.getUserOrThrow(userId);
        user.updateUserInfo(requestDto.getEmail(), requestDto.getPhone());
    }


    @Override
    @Transactional
    public GetUserResponseDto getUserById(Integer userId) {
        User user = entityFinder.getUserOrThrow(userId);
        return new GetUserResponseDto(user);
    }


    @Override
    @Transactional
    public void withdrawUser(Integer userId, User currentUser) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("본인만 탈퇴할 수 있습니다.");
        }

        User user = entityFinder.getUserOrThrow(userId);
        userRepository.delete(user);
    }


    @Override
    @Transactional(readOnly = true)
    public GetUserResponseDto getUserByLoginId(String loginId) {
        User user = entityFinder.getLoginIdOrThrow(loginId);
        return new GetUserResponseDto(user);
    }


    @Override
    @Transactional(readOnly = true)
    public GetUserResponseDto getRandomUserForVisit(Integer currentUserId) {
        long count = userRepository.countByIsDeletedFalseAndIdIsNot(currentUserId);

        if (count == 0) {
            return null;
        }

        int randomOffset = new Random().nextInt((int) count);
        Page<User> userPage = userRepository.findByIsDeletedFalseAndIdIsNot(currentUserId, PageRequest.of(randomOffset, 1));

        if (!userPage.hasContent()) {
            return null;
        }
        User randomUser = userPage.getContent().get(0);

        return new GetUserResponseDto(randomUser);
    }

    @Override
    @Transactional(readOnly = true)
    public GetUserResponseDto getRandomUserForRecommendation(Integer currentUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        Set<Integer> excludedIds = ilchonRepository
                .findAllByUserAndStatus(currentUser, Ilchon.IlchonStatus.ACCEPTED)
                .stream()
                .map(ilchon -> ilchon.getFriend().getId())
                .collect(Collectors.toSet());
        excludedIds.add(currentUserId);

        long count = userRepository.countByIsDeletedFalseAndIdNotIn(excludedIds);

        if (count == 0) {
            return null;
        }

        int randomOffset = new Random().nextInt((int) count);
        Page<User> userPage = userRepository.findByIsDeletedFalseAndIdNotIn(excludedIds, PageRequest.of(randomOffset, 1));

        if (!userPage.hasContent()) {
            return null;
        }

        User randomUser = userPage.getContent().get(0);
        GetUserResponseDto userResponseDto = new GetUserResponseDto(randomUser);

        try {
            UserProfile activeProfile = entityFinder.getActiveUserProfileOrThrow(randomUser.getId());
            userResponseDto.setImageUrl(activeProfile.getImageUrl());
        } catch (IllegalArgumentException e) {
            userResponseDto.setImageUrl(null);
        }

        Integer mutualCount = ilchonService.countMutualIlchons(currentUserId, randomUser.getId());
        userResponseDto.setMutualIlchons(mutualCount);

        return userResponseDto;
    }

}

