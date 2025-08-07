package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.user.dto.response.schema.ProfileHistoryDto;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.entity.UserProfile;
import com.hyunjoying.cyworld.user.repository.UserProfileRepository;
import com.hyunjoying.cyworld.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private static final int MAX_PROFILE_HISTORY_SIZE = 20;


    @Override
    @Transactional(readOnly = true)
    public GetProfileResponseDto getProfile(Integer userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다." + userId));

        UserProfile activeProfile = userProfileRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 프로필을 찾을 수 없습니다. User ID: " + userId));

        List<UserProfile> allProfiles = userProfileRepository.findAllByUserOrderByCreatedAtDesc(user);

        List<ProfileHistoryDto> profileHistoryList = allProfiles.stream()
                .map(profile -> new ProfileHistoryDto(
                        profile.getId(),
                        profile.getImageUrl(),
                        profile.getBio(),
                        profile.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                ))
                .collect(Collectors.toList());


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
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다." + userId));

        UserProfile activeProfile = userProfileRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 프로필을 찾을 수 없습니다. User ID: " + userId));

        boolean isImageSame = requestDto.getProfileImageUrl() != null && requestDto.getProfileImageUrl().equals(activeProfile.getImageUrl());
        boolean isBioSame = requestDto.getBio() != null && requestDto.getBio().equals(activeProfile.getBio());

        if (isImageSame && isBioSame) return;

        activeProfile.setActive(false);
        activeProfile.setUpdatedBy(userId);

        UserProfile newProfile = new UserProfile();
        newProfile.setUser(user);
        newProfile.setImageUrl(requestDto.getProfileImageUrl() != null ? requestDto.getProfileImageUrl() : activeProfile.getImageUrl());
        newProfile.setBio(requestDto.getBio() != null ? requestDto.getBio() : activeProfile.getBio());
        newProfile.setActive(true);
        newProfile.setCreatedBy(userId);
        newProfile.setUpdatedBy(userId);

        userProfileRepository.save(newProfile);

        long profileCount = userProfileRepository.countByUser(user);

        if (profileCount > MAX_PROFILE_HISTORY_SIZE) {
            userProfileRepository.findFirstByUserOrderByCreatedAtAsc(user)
                    .ifPresent(oldestProfile -> userProfileRepository.delete(oldestProfile));
        }
    }
}
