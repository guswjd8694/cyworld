package com.hyunjoying.cyworld.domain.user.repository;

import com.hyunjoying.cyworld.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByLoginIdAndIsDeletedFalse(String loginId);

    boolean existsByEmailAndIsDeletedFalse(String email);

    Optional<User> findByLoginIdAndIsDeletedFalse(String loginId);

    Optional<User> findByNameAndEmailAndIsDeletedFalse(String name, String email);

    Optional<User> findByLoginIdAndEmailAndIsDeletedFalse(String loginId, String email);
}
