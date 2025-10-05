package com.hyunjoying.cyworld.domain.user.repository;

import com.hyunjoying.cyworld.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLoginIdAndIsDeletedFalse(String loginId);

    Optional<User> findByNameAndEmailAndIsDeletedFalse(String name, String email);

    Optional<User> findByLoginIdAndEmailAndIsDeletedFalse(String loginId, String email);

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);

    long countByIsDeletedFalseAndIdIsNot(Integer excludeUserId);
    Page<User> findByIsDeletedFalseAndIdIsNot(Integer excludeUserId, Pageable pageable);

    long countByIsDeletedFalseAndIdNotIn(Set<Integer> excludedIds);
    Page<User> findByIsDeletedFalseAndIdNotIn(Set<Integer> excludedIds, Pageable pageable);

}
