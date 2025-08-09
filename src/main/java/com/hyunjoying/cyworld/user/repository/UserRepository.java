package com.hyunjoying.cyworld.user.repository;

import com.hyunjoying.cyworld.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByNameAndEmail(String name, String email);
    Optional<User> findByLoginIdAndEmail(String loginId, String email);
}
