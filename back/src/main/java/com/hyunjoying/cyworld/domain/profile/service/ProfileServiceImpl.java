package com.hyunjoying.cyworld.domain.profile.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.profile.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.domain.profile.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.domain.profile.dto.response.schema.ProfileHistoryDto;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.profile.entity.UserProfile;
import com.hyunjoying.cyworld.domain.profile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserProfileRepository userProfileRepository;
    private final EntityFinder entityFinder;


    @Override
    @Transactional(readOnly = true)
    public GetProfileResponseDto getProfile(Integer userId, Integer limit){
        User user = entityFinder.getUserOrThrow(userId);
        UserProfile activeProfile = entityFinder.getActiveUserProfileOrThrow(userId);

        List<UserProfile> allProfiles = userProfileRepository.findAllByUserOrderByCreatedAtDesc(user);
        List<ProfileHistoryDto> profileHistoryList = allProfiles.stream()
                .limit(limit)
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
        User user = entityFinder.getUserOrThrow(userId);
        UserProfile activeProfile = entityFinder.getActiveUserProfileOrThrow(userId);

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
    }
}
