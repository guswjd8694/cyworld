package com.hyunjoying.cyworld.user.repository;

import com.hyunjoying.cyworld.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
