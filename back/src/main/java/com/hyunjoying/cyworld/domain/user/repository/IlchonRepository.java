package com.hyunjoying.cyworld.domain.user.repository;

import com.hyunjoying.cyworld.domain.user.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IlchonRepository extends JpaRepository<Ilchon, Integer> {
    Optional<Ilchon> findByUserAndFriend(User user, User friend);

    List<Ilchon> findAllByUserAndStatus(User user, String status);

    List<Ilchon> findAllByFriendAndStatus(User friend, String status);
}
