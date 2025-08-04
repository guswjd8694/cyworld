package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.schma.ProfileHistoryDto;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.entity.UserProfile;
import com.hyunjoying.cyworld.user.repository.UserProfileRepository;
import com.hyunjoying.cyworld.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;


    @Override
    @Transactional(readOnly = true)
    public GetProfileResponseDto getProfile(Integer userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다." + userId));

        UserProfile activeProfile = userProfileRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 프로필을 찾을 수 없습니다. User ID: " + userId));


        List<ProfileHistoryDto> profileHistoryList = List.of();

        return new GetProfileResponseDto(
                profileHistoryList,
                user.getName(),
                user.getGender().toString(),
                LocalDate.parse(user.getBirth()),
                user.getEmail(),
                activeProfile.getImageUrl(),
                activeProfile.getBio()
        );
    }


    @Override
    @Transactional
    public void updateProfile(Integer userId, UpdateProfileRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다: " + userId));

        UserProfile activeProfile = userProfileRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 프로필을 찾을 수 없습니다. User ID: " + userId));

        if (requestDto.getProfileImageUrl() != null) {
            activeProfile.setImageUrl(requestDto.getProfileImageUrl());
        }

        if (requestDto.getBio() != null) {
            activeProfile.setBio(requestDto.getBio());
        }
    }
}
