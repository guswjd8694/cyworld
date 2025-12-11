package com.hyunjoying.cyworld.domain.ilchon.repository;

import com.hyunjoying.cyworld.domain.ilchon.entity.IlchonRequest;
import com.hyunjoying.cyworld.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IlchonRequestRepository extends JpaRepository<IlchonRequest, Integer> {

    @EntityGraph(attributePaths = {"fromUser"})
    List<IlchonRequest> findAllByToUserAndStatus(User toUser, IlchonRequest.IlchonRequestStatus status);

    @EntityGraph(attributePaths = {"toUser"})
    List<IlchonRequest> findAllByFromUserAndStatus(User fromUser, IlchonRequest.IlchonRequestStatus status);

    Optional<IlchonRequest> findByFromUserAndToUserAndStatus(User fromUser, User toUser, IlchonRequest.IlchonRequestStatus status);


    @Query("SELECT ir FROM IlchonRequest ir WHERE " +
            "((ir.fromUser = :userA AND ir.toUser = :userB) OR (ir.fromUser = :userB AND ir.toUser = :userA)) " +
            "AND ir.status = :status")
    Optional<IlchonRequest> findExistingPendingRequest(
            @Param("userA") User userA,
            @Param("userB") User userB,
            @Param("status") IlchonRequest.IlchonRequestStatus status);


    @Override
    @EntityGraph(attributePaths = {"fromUser", "toUser"})
    Optional<IlchonRequest> findById(Integer id);
}