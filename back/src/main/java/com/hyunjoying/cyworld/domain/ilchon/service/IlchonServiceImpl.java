package com.hyunjoying.cyworld.domain.ilchon.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.RequestIlchonDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.UpdateIlchonNicknameRequestDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonRequestResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IlchonServiceImpl implements IlchonService {
    private final IlchonRepository ilchonRepository;
    private final EntityFinder entityFinder;

    @Override
    @Transactional
    public void requestIlchon(Integer currentUserId, RequestIlchonDto requestDto) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(requestDto.getTargetUserId());

        if (currentUserId.equals(requestDto.getTargetUserId())) {
            throw new IllegalArgumentException("자기 자신에게 일촌을 신청할 수 없습니다.");
        }

        Optional<Ilchon> latestInteraction = ilchonRepository.findTopByUserAndFriendOrderByCreatedAtDesc(currentUser, targetUser)
                .or(() -> ilchonRepository.findTopByUserAndFriendOrderByCreatedAtDesc(targetUser, currentUser));

        if (latestInteraction.isPresent()) {
            Ilchon lastIlchon = latestInteraction.get();
            if (lastIlchon.getStatus() == Ilchon.IlchonStatus.ACCEPTED && lastIlchon.isActive()) {
                throw new IllegalArgumentException("이미 일촌 관계입니다.");
            }
            if (lastIlchon.getStatus() == Ilchon.IlchonStatus.PENDING && lastIlchon.isActive()) {
                throw new IllegalArgumentException("이미 처리 대기 중인 일촌 신청이 있습니다.");
            }
        }

        Ilchon newRequest = new Ilchon();
        newRequest.setUser(currentUser);
        newRequest.setFriend(targetUser);
        newRequest.setStatus(Ilchon.IlchonStatus.PENDING);
        newRequest.setUserNickname(requestDto.getUserNickname());
        newRequest.setFriendNickname(requestDto.getFriendNickname());
        newRequest.setRequestMessage(requestDto.getRequestMessage());
        ilchonRepository.save(newRequest);
    }

    @Override
    @Transactional
    public void acceptIlchon(Integer currentUserId, Integer ilchonRequestId) {
        Ilchon request = entityFinder.getIlchonOrThrow(ilchonRequestId);

        if (!request.getFriend().getId().equals(currentUserId)) {
            throw new AccessDeniedException("일촌 수락 권한이 없습니다.");
        }
        if (request.getStatus() != Ilchon.IlchonStatus.PENDING || !request.isActive()) {
            throw new IllegalStateException("이미 처리된 일촌 요청입니다.");
        }

        request.setStatus(Ilchon.IlchonStatus.ACCEPTED);

        Ilchon reciprocalIlchon = new Ilchon();
        reciprocalIlchon.setUser(request.getFriend());
        reciprocalIlchon.setFriend(request.getUser());
        reciprocalIlchon.setStatus(Ilchon.IlchonStatus.ACCEPTED);
        reciprocalIlchon.setUserNickname(request.getFriendNickname());
        reciprocalIlchon.setFriendNickname(request.getUserNickname());

        ilchonRepository.save(reciprocalIlchon);
    }

    @Override
    @Transactional
    public void updateIlchonNickname(Integer currentUserId, UpdateIlchonNicknameRequestDto requestDto) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(requestDto.getTargetUserId());

        Ilchon mySideOld = ilchonRepository.findByUserAndFriendAndStatusAndIsActiveTrue(currentUser, targetUser, Ilchon.IlchonStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 일촌 관계를 찾을 수 없습니다."));
        Ilchon theirSideOld = ilchonRepository.findByUserAndFriendAndStatusAndIsActiveTrue(targetUser, currentUser, Ilchon.IlchonStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 일촌 관계를 찾을 수 없습니다."));

        mySideOld.setActive(false);
        theirSideOld.setActive(false);

        Ilchon mySideNew = new Ilchon();
        mySideNew.setUser(currentUser);
        mySideNew.setFriend(targetUser);
        mySideNew.setStatus(Ilchon.IlchonStatus.ACCEPTED);
        mySideNew.setUserNickname(requestDto.getMyNicknameForFriend());
        mySideNew.setFriendNickname(requestDto.getFriendNicknameForMe());

        Ilchon theirSideNew = new Ilchon();
        theirSideNew.setUser(targetUser);
        theirSideNew.setFriend(currentUser);
        theirSideNew.setStatus(Ilchon.IlchonStatus.ACCEPTED);
        theirSideNew.setUserNickname(requestDto.getFriendNicknameForMe());
        theirSideNew.setFriendNickname(requestDto.getMyNicknameForFriend());

        ilchonRepository.saveAll(List.of(mySideNew, theirSideNew));
    }

    @Override
    @Transactional
    public void rejectIlchon(Integer currentUserId, Integer ilchonRequestId) {
        Ilchon request = entityFinder.getIlchonOrThrow(ilchonRequestId);
        if (!request.getFriend().getId().equals(currentUserId)) {
            throw new AccessDeniedException("일촌 거절 권한이 없습니다.");
        }
        if (request.getStatus() != Ilchon.IlchonStatus.PENDING || !request.isActive()) {
            throw new IllegalStateException("이미 처리된 일촌 요청입니다.");
        }
        request.setStatus(Ilchon.IlchonStatus.REJECTED);
        request.setActive(false);
    }

    @Override
    @Transactional
    public void cancelIlchonRequest(Integer currentUserId, Integer ilchonRequestId) {
        Ilchon request = entityFinder.getIlchonOrThrow(ilchonRequestId);
        if (!request.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("일촌 신청을 취소할 권한이 없습니다.");
        }
        if (request.getStatus() != Ilchon.IlchonStatus.PENDING || !request.isActive()) {
            throw new IllegalStateException("이미 처리된 일촌 요청입니다.");
        }
        request.setStatus(Ilchon.IlchonStatus.CANCELED);
        request.setActive(false);
    }

    @Override
    @Transactional
    public void breakIlchon(Integer currentUserId, Integer targetUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(targetUserId);

        Ilchon mySide = ilchonRepository.findByUserAndFriendAndStatusAndIsActiveTrue(currentUser, targetUser, Ilchon.IlchonStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("두 사용자 사이에 활성화된 일촌 관계가 존재하지 않습니다."));
        Ilchon theirSide = ilchonRepository.findByUserAndFriendAndStatusAndIsActiveTrue(targetUser, currentUser, Ilchon.IlchonStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("두 사용자 사이에 활성화된 일촌 관계가 존재하지 않습니다."));

        mySide.setStatus(Ilchon.IlchonStatus.BROKEN);
        mySide.setActive(false);
        theirSide.setStatus(Ilchon.IlchonStatus.BROKEN);
        theirSide.setActive(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetIlchonResponseDto> getIlchons(Integer userId) {
        User user = entityFinder.getUserOrThrow(userId);
        List<Ilchon> ilchons = ilchonRepository.findAllByUserAndStatusAndIsActiveTrue(user, Ilchon.IlchonStatus.ACCEPTED);
        return ilchons.stream()
                .map(ilchon -> new GetIlchonResponseDto(
                        ilchon.getId(),
                        ilchon.getFriend(),
                        ilchon.getUserNickname(),
                        ilchon.getFriendNickname(),
                        null
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetIlchonRequestResponseDto> getReceivedIlchonRequests(Integer currentUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        List<Ilchon> requests = ilchonRepository.findAllByFriendAndStatusAndIsActiveTrue(currentUser, Ilchon.IlchonStatus.PENDING);
        return requests.stream().map(GetIlchonRequestResponseDto::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetIlchonResponseDto> getSentIlchonRequests(Integer currentUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        List<Ilchon> requests = ilchonRepository.findAllByUserAndStatusAndIsActiveTrue(currentUser, Ilchon.IlchonStatus.PENDING);
        return requests.stream()
                .map(req -> new GetIlchonResponseDto(
                        req.getId(),
                        req.getFriend(),
                        req.getUserNickname(),
                        req.getFriendNickname(),
                        req.getRequestMessage()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String getIlchonStatus(Integer currentUserId, Integer targetUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(targetUserId);

        Optional<Ilchon> latestInteraction = ilchonRepository.findTopByUserAndFriendOrderByCreatedAtDesc(currentUser, targetUser)
                .or(() -> ilchonRepository.findTopByUserAndFriendOrderByCreatedAtDesc(targetUser, currentUser));

        if (latestInteraction.isEmpty() || !latestInteraction.get().isActive()) {
            return "NONE";
        }

        Ilchon lastIlchon = latestInteraction.get();
        if (lastIlchon.getStatus() == Ilchon.IlchonStatus.PENDING) {
            if (lastIlchon.getUser().getId().equals(targetUserId)) {
                return "PENDING_RECEIVED";
            } else {
                return "PENDING";
            }
        }

        return lastIlchon.getStatus().name();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateRelationshipDegree(Integer currentUserId, Integer targetUserId) {
        if (currentUserId.equals(targetUserId)) return 0;
        return ilchonRepository.findShortestPathDegree(currentUserId, targetUserId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countMutualIlchons(Integer currentUserId, Integer targetUserId) {
        Set<User> currentUserIlchons = getAcceptedIlchons(currentUserId);
        Set<User> targetUserIlchons = getAcceptedIlchons(targetUserId);
        currentUserIlchons.retainAll(targetUserIlchons);
        return currentUserIlchons.size();
    }

    private Set<User> getAcceptedIlchons(Integer userId) {
        User user = entityFinder.getUserOrThrow(userId);
        Stream<User> friendsAsUser = ilchonRepository.findAllByUserAndStatusAndIsActiveTrue(user, Ilchon.IlchonStatus.ACCEPTED)
                .stream().filter(Objects::nonNull).map(Ilchon::getFriend).filter(Objects::nonNull);
        Stream<User> friendsAsFriend = ilchonRepository.findAllByFriendAndStatusAndIsActiveTrue(user, Ilchon.IlchonStatus.ACCEPTED)
                .stream().filter(Objects::nonNull).map(Ilchon::getUser).filter(Objects::nonNull);
        return Stream.concat(friendsAsUser, friendsAsFriend).collect(Collectors.toSet());
    }
}

