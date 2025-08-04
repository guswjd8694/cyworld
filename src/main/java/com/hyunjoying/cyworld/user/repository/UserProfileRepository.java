package com.hyunjoying.cyworld.user.repository;

import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Optional<UserProfile> findByUserAndActiveTrue(User user);
}
