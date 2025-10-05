package com.hyunjoying.cyworld.domain.profile.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.profile.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.domain.profile.dto.response.GetProfileResponseDto;
import com.hyunjoying.cyworld.domain.profile.dto.response.schema.ProfileHistoryDto;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.profile.entity.UserProfile;
import com.hyunjoying.cyworld.domain.profile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserProfileRepository userProfileRepository;
    private final EntityFinder entityFinder;

    @Override
    @Transactional(readOnly = true)
    public GetProfileResponseDto getProfile(Integer userId, Integer limit) {
        User user = entityFinder.getUserOrThrow(userId);
        UserProfile activeProfile = entityFinder.getActiveUserProfileOrThrow(userId);

        Pageable pageable = PageRequest.of(0, limit);
        List<UserProfile> recentProfiles = userProfileRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        List<ProfileHistoryDto> profileHistoryList = recentProfiles.stream()
                .map(ProfileHistoryDto::new)
                .collect(Collectors.toList());

        return new GetProfileResponseDto(user, activeProfile, profileHistoryList);
    }

    @Override
    @Transactional
    public GetProfileResponseDto updateProfile(Integer userId, UpdateProfileRequestDto requestDto) {
        User user = entityFinder.getUserOrThrow(userId);
        UserProfile activeProfile = entityFinder.getActiveUserProfileOrThrow(userId);

        boolean isImageSame = requestDto.getImageUrl() != null && requestDto.getImageUrl().equals(activeProfile.getImageUrl());
        boolean isBioSame = requestDto.getBio() != null && requestDto.getBio().equals(activeProfile.getBio());

        if (isImageSame && isBioSame) {
            return new GetProfileResponseDto(user, activeProfile, null);
        }

        activeProfile.deactivate();

        UserProfile newProfile = UserProfile.createNewVersion(user, requestDto, activeProfile);

        UserProfile savedProfile = userProfileRepository.save(newProfile);

        return new GetProfileResponseDto(user, savedProfile, null);
    }
}
