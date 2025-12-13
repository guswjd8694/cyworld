package com.hyunjoying.cyworld.domain.ilchon.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.UpdateIlchonNicknameRequestDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonRelationshipResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
import com.hyunjoying.cyworld.domain.ilchon.entity.IlchonRequest;
import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRepository;
import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRequestRepository;
import com.hyunjoying.cyworld.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IlchonServiceImpl implements IlchonService {

    private final IlchonRepository ilchonRepository;
    private final EntityFinder entityFinder;
    private final IlchonRequestRepository ilchonRequestRepository;


    @Override
    @Transactional
    public void breakIlchon(Integer currentUserId, Integer targetUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(targetUserId);

        Ilchon ilchon1 = ilchonRepository.findTopByUserAndFriendAndStatusOrderByCreatedAtDesc(
                        currentUser, targetUser, Ilchon.IlchonStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalStateException("일촌 관계가 아닙니다."));

        Ilchon ilchon2 = ilchonRepository.findTopByUserAndFriendAndStatusOrderByCreatedAtDesc(
                        targetUser, currentUser, Ilchon.IlchonStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalStateException("일촌 관계가 아닙니다."));

        ilchon1.setStatus(Ilchon.IlchonStatus.BROKEN);
        ilchon2.setStatus(Ilchon.IlchonStatus.BROKEN);

    }

    @Override
    public List<GetIlchonResponseDto> getIlchons(Integer userId) {
        User user = entityFinder.getUserOrThrow(userId);

        List<Ilchon> myIlchons = ilchonRepository.findLatestByUserGroupByFriendAndStatus(
                user.getId(),
                Ilchon.IlchonStatus.ACCEPTED.name()
        );

        Set<Integer> friendIds = myIlchons.stream()
                .map(ilchon -> ilchon.getFriend().getId())
                .collect(Collectors.toSet());

        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Ilchon> friendToUserIlchons = ilchonRepository.findLatestByUserInAndFriendAndStatus(
                friendIds,
                userId,
                Ilchon.IlchonStatus.ACCEPTED.name()
        );

        Map<Integer, String> friendNicknamesMap = friendToUserIlchons.stream()
                .collect(Collectors.toMap(
                        ilchon -> ilchon.getUser().getId(),
                        Ilchon::getNickname,
                        (oldValue, newValue) -> newValue
                ));

        return myIlchons.stream().map(myIlchon -> {
            User friend = myIlchon.getFriend();
            String myNickname = myIlchon.getNickname();

            String friendNickname = friendNicknamesMap.get(friend.getId());

            return new GetIlchonResponseDto(
                    myIlchon.getId(),
                    friend,
                    myNickname,
                    friendNickname
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateIlchonNickname(Integer currentUserId, UpdateIlchonNicknameRequestDto requestDto) {

        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(requestDto.getTargetUserId());

        Ilchon ilchonAtoB = ilchonRepository.findTopByUserAndFriendAndStatusOrderByCreatedAtDesc(
                        currentUser, targetUser, Ilchon.IlchonStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalStateException("일촌 관계가 아닙니다. (A->B)"));

        Ilchon ilchonBtoA = ilchonRepository.findTopByUserAndFriendAndStatusOrderByCreatedAtDesc(
                        targetUser, currentUser, Ilchon.IlchonStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalStateException("일촌 관계가 아닙니다. (B->A)"));

        ilchonAtoB.setNickname(requestDto.getNicknameForToUser());
        ilchonBtoA.setNickname(requestDto.getNicknameForFromUser());
    }


    @Override
    public GetIlchonRelationshipResponseDto getRelationship(Integer currentUserId, Integer targetUserId) {
        User userA = entityFinder.getUserOrThrow(currentUserId);
        User userB = entityFinder.getUserOrThrow(targetUserId);

        if (userA.equals(userB)) {
            return new GetIlchonRelationshipResponseDto(0, null, null);
        }

        Optional<Ilchon> ilchonAtoB = ilchonRepository.findTopByUserAndFriendOrderByCreatedAtDesc(userA, userB);
        Optional<Ilchon> ilchonBtoA = ilchonRepository.findTopByUserAndFriendOrderByCreatedAtDesc(userB, userA);


        if (ilchonAtoB.isPresent() && ilchonAtoB.get().getStatus() == Ilchon.IlchonStatus.ACCEPTED &&
                ilchonBtoA.isPresent() && ilchonBtoA.get().getStatus() == Ilchon.IlchonStatus.ACCEPTED) {

            return new GetIlchonRelationshipResponseDto(
                    1,
                    ilchonBtoA.get().getNickname(),
                    ilchonAtoB.get().getNickname()
            );
        }


        Optional<IlchonRequest> pendingRequest = ilchonRequestRepository
                .findExistingPendingRequest(userA, userB, IlchonRequest.IlchonRequestStatus.PENDING);

        if (pendingRequest.isPresent()) {
            return new GetIlchonRelationshipResponseDto(-2, null, null);
        }

        Integer degree = ilchonRepository.findShortestPathDegree(currentUserId, targetUserId)
                .orElse(-1);

        return new GetIlchonRelationshipResponseDto(degree, null, null);
    }



    @Override
    public Integer countMutualIlchons(Integer currentUserId, Integer targetUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(targetUserId);

        List<Ilchon> myIlchons = ilchonRepository.findLatestByUserGroupByFriendAndStatus(
                currentUser.getId(), Ilchon.IlchonStatus.ACCEPTED.name());

        List<Ilchon> targetUserIlchons = ilchonRepository.findLatestByUserGroupByFriendAndStatus(
                targetUser.getId(), Ilchon.IlchonStatus.ACCEPTED.name());

        Set<Integer> myFriendIds = myIlchons.stream()
                .map(ilchon -> ilchon.getFriend().getId())
                .collect(Collectors.toSet());

        Set<Integer> targetFriendIds = targetUserIlchons.stream()
                .map(ilchon -> ilchon.getFriend().getId())
                .collect(Collectors.toSet());

        myFriendIds.retainAll(targetFriendIds);

        return myFriendIds.size();
    }
}