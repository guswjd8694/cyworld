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

// ⭐️ 참고: IlchonRequestRepository는 별도 파일로 생성
public interface IlchonRepository extends JpaRepository<Ilchon, Integer> {

    @EntityGraph(attributePaths = {"user", "friend"})
    Optional<Ilchon> findByUserAndFriendAndStatus(User user, User friend, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    List<Ilchon> findByUserAndFriendInAndStatus(User user, Set<User> friends, Ilchon.IlchonStatus status);

    @EntityGraph(attributePaths = {"user", "friend"})
    List<Ilchon> findAllByUserAndStatus(User user, Ilchon.IlchonStatus status);


    @EntityGraph(attributePaths = {"user", "friend"})
    Optional<Ilchon> findTopByUserAndFriendOrderByCreatedAtDesc(User user, User friend);

    @EntityGraph(attributePaths = {"user", "friend"})
    List<Ilchon> findByUserInAndFriendAndStatus(Set<User> users, User friend, Ilchon.IlchonStatus status);


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