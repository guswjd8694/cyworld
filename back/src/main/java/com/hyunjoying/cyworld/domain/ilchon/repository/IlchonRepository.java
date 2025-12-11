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
    Optional<Ilchon> findTopByUserAndFriendOrderByCreatedAtDesc(User user, User friend);


    @EntityGraph(attributePaths = {"user", "friend"})
    Optional<Ilchon> findByUserAndFriendAndStatus(User user, User friend, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    List<Ilchon> findByUserAndFriendInAndStatus(User user, Set<User> friends, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    List<Ilchon> findAllByUserAndStatus(User user, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    Optional<Ilchon> findTopByUserAndFriendAndStatusOrderByCreatedAtDesc(User user, User friend, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    List<Ilchon> findByUserInAndFriendAndStatus(Set<User> users, User friend, Ilchon.IlchonStatus status);


    @Query(
            value = "SELECT i.* FROM ilchons i " +
                    "INNER JOIN (" +
                    "    SELECT user_id, MAX(created_at) as max_created_at " +
                    "    FROM ilchons " +
                    "    WHERE user_id IN (:userIds) AND friend_id = :friendId AND status = :status " +
                    "    GROUP BY user_id" +
                    ") AS latest_ilchon " +
                    "ON i.user_id = latest_ilchon.user_id AND i.created_at = latest_ilchon.max_created_at " +
                    "WHERE i.friend_id = :friendId AND i.status = :status",
            nativeQuery = true
    )
    List<Ilchon> findLatestByUserInAndFriendAndStatus(
            @Param("userIds") Set<Integer> userIds,
            @Param("friendId") Integer friendId,
            @Param("status") String status);


    @Query(
            value = "SELECT i.* FROM ilchons i " +
                    "INNER JOIN (" +
                    "    SELECT friend_id, MAX(created_at) as max_created_at " +
                    "    FROM ilchons " +
                    "    WHERE user_id = :userId AND status = :status " +
                    "    GROUP BY friend_id" +
                    ") AS latest_ilchon " +
                    "ON i.friend_id = latest_ilchon.friend_id AND i.created_at = latest_ilchon.max_created_at " +
                    "WHERE i.user_id = :userId AND i.status = :status",
            nativeQuery = true
    )
    List<Ilchon> findLatestByUserGroupByFriendAndStatus(
            @Param("userId") Integer userId,
            @Param("status") String status);


    @Query(
            value = "WITH RECURSIVE FriendPath AS (" +
                    "    SELECT friend_id, 1 AS degree FROM ilchons WHERE user_id = :startUserId AND status = 'ACCEPTED'" +
                    "    UNION ALL" +
                    "    SELECT il.friend_id, fp.degree + 1 FROM FriendPath fp JOIN ilchons il ON fp.friend_id = il.user_id WHERE fp.degree < 4 AND il.status = 'ACCEPTED'" +
                    ")" +
                    "SELECT MIN(degree) FROM FriendPath WHERE friend_id = :endUserId",
            nativeQuery = true
    )
    Optional<Integer> findShortestPathDegree(@Param("startUserId") Integer startUserId, @Param("endUserId") Integer endUserId);
}