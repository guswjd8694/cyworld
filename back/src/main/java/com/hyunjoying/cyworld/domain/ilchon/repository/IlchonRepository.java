package com.hyunjoying.cyworld.domain.ilchon.repository;

import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IlchonRepository extends JpaRepository<Ilchon, Integer> {

    @EntityGraph(attributePaths = {"user", "friend"})
    Optional<Ilchon> findByUserAndFriendAndStatusAndIsActiveTrue(User user, User friend, Ilchon.IlchonStatus status);

    List<Ilchon> findByUserAndFriendInAndStatusAndIsActiveTrue(User user, Set<User> friends, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    List<Ilchon> findAllByUserAndStatusAndIsActiveTrue(User user, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    List<Ilchon> findAllByFriendAndStatusAndIsActiveTrue(User friend, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    Optional<Ilchon> findTopByUserAndFriendOrderByCreatedAtDesc(User user, User friend);


    @Query(
        value = "WITH RECURSIVE FriendPath AS (" +
                "    SELECT friend_id, 1 AS degree FROM ilchons WHERE user_id = :startUserId AND status = 'ACCEPTED' AND is_active = true" +
                "    UNION ALL" +
                "    SELECT il.friend_id, fp.degree + 1 FROM FriendPath fp JOIN ilchons il ON fp.friend_id = il.user_id WHERE fp.degree < 4 AND il.status = 'ACCEPTED' AND il.is_active = true" +
                "), " +
                "ReverseFriendPath AS (" +
                "    SELECT user_id, 1 AS degree FROM ilchons WHERE friend_id = :startUserId AND status = 'ACCEPTED' AND is_active = true" +
                "    UNION ALL" +
                "    SELECT il.user_id, rfp.degree + 1 FROM ReverseFriendPath rfp JOIN ilchons il ON rfp.user_id = il.friend_id WHERE rfp.degree < 4 AND il.status = 'ACCEPTED' AND il.is_active = true" +
                ")" +
                "SELECT MIN(degree) FROM (" +
                "    SELECT degree FROM FriendPath WHERE friend_id = :endUserId" +
                "    UNION ALL" +
                "    SELECT degree FROM ReverseFriendPath WHERE user_id = :endUserId" +
                ") AS degrees",
        nativeQuery = true
    )
    Optional<Integer> findShortestPathDegree(@Param("startUserId") Integer startUserId, @Param("endUserId") Integer endUserId);
}

