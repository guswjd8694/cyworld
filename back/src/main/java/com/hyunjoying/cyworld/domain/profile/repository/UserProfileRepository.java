package com.hyunjoying.cyworld.domain.profile.repository;

import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.profile.entity.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Optional<UserProfile> findFirstByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);
    List<UserProfile> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
