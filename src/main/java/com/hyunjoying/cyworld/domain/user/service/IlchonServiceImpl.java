package com.hyunjoying.cyworld.domain.user.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.user.dto.request.RequestIlchonDto;
import com.hyunjoying.cyworld.domain.user.dto.response.GetIlchonResponseDto;
import com.hyunjoying.cyworld.domain.user.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.user.repository.IlchonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IlchonServiceImpl implements IlchonService {
    private final IlchonRepository ilchonRepository;
    private final EntityFinder entityFinder;

    // 일촌 신청
    @Override
    @Transactional
    public void requestIlchon(Integer currentUserId, RequestIlchonDto requestDto) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(requestDto.getTargetUserId());

        if (currentUserId.equals(requestDto.getTargetUserId())) {
            throw new IllegalArgumentException("자기 자신에게 일촌을 신청할 수 없습니다.");
        }

        ilchonRepository.findByUserAndFriend(currentUser, targetUser)
                .ifPresent(ilchon -> {throw new IllegalArgumentException("이미 일촌 신청을 했거나 일촌 관계입니다.");});

        Ilchon newRequest = new Ilchon();
        newRequest.setUser(currentUser);
        newRequest.setFriend(targetUser);
        newRequest.setStatus("PENDING");
        newRequest.setCreatedBy(currentUserId);
        newRequest.setUpdatedBy(currentUserId);
        newRequest.setUserNickname(requestDto.getUserNickname());
        newRequest.setFriendNickname(requestDto.getFriendNickname());
        newRequest.setRequestMessage(requestDto.getRequestMessage());

        ilchonRepository.save(newRequest);
    }


    // 일촌 신청 수락
    @Override
    @Transactional
    public void acceptIlchon(Integer currentUserId, Integer ilchonRequestId) {
        Ilchon request = entityFinder.getIlchonOrThrow(ilchonRequestId);

        if (!request.getFriend().getId().equals(currentUserId)) {
            throw new SecurityException("일촌 수락 권한이 없습니다.");
        }

        request.setStatus("ACCEPTED");
        request.setUpdatedBy(currentUserId);

        Ilchon reciprocalIlchon = new Ilchon();
        reciprocalIlchon.setUser(request.getFriend());
        reciprocalIlchon.setFriend(request.getUser());
        reciprocalIlchon.setStatus("ACCEPTED");
        reciprocalIlchon.setCreatedBy(currentUserId);
        reciprocalIlchon.setUserNickname(request.getFriendNickname());
        reciprocalIlchon.setFriendNickname(request.getUserNickname());

        ilchonRepository.save(reciprocalIlchon);
    }


    // 일촌 신청 거절
    @Override
    @Transactional
    public void rejectIlchon(Integer currentUserId, Integer ilchonRequestId) {
        Ilchon request = entityFinder.getIlchonOrThrow(ilchonRequestId);

        if (!request.getFriend().getId().equals(currentUserId)){
            throw new AccessDeniedException("일촌 거절 권한이 없습니다.");
        }

        ilchonRepository.delete(request);
    }


    // 일촌 신청 취소
    @Override
    @Transactional
    public void cancelIlchonRequest(Integer currentUserId, Integer ilchonRequestId) {
        Ilchon request = entityFinder.getIlchonOrThrow(ilchonRequestId);

        if (!request.getUser().getId().equals(currentUserId)){
            throw new AccessDeniedException("일촌 신청을 취소할 권한이 없습니다.");
        }

        if (!"PENDING".equals(request.getStatus())){
            throw new IllegalStateException("이미 처리된 일촌 요청입니다.");
        }
        ilchonRepository.delete(request);
    }



    // 일촌 끊기
    @Override
    @Transactional
    public void breakIlchon(Integer currentUserId, Integer targetUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(targetUserId);

        Optional<Ilchon> mySide = ilchonRepository.findByUserAndFriend(currentUser, targetUser);
        Optional<Ilchon> theirSide = ilchonRepository.findByUserAndFriend(targetUser, currentUser);

        if (mySide.isEmpty() || theirSide.isEmpty()) {
            throw new IllegalArgumentException("두 사용자 사이에 일촌 관계가 존재하지 않습니다.");
        }

        mySide.ifPresent(ilchonRepository::delete);
        theirSide.ifPresent(ilchonRepository::delete);
    }


    // 일촌 목록 보기
    @Override
    @Transactional(readOnly = true)
    public List<GetIlchonResponseDto> getIlchon(Integer userId) {
        User user = entityFinder.getUserOrThrow(userId);
        List<Ilchon> ilchons = ilchonRepository.findAllByUserAndStatus(user, "ACCEPTED");

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


    // 받은 일촌 신청 보기
    @Override
    @Transactional(readOnly = true)
    public List<GetIlchonResponseDto> getReceivedIlchonRequests(Integer currentUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        List<Ilchon> requests = ilchonRepository.findAllByFriendAndStatus(currentUser, "PENDING");

        return requests.stream()
                .map(request -> new GetIlchonResponseDto(
                        request.getId(),
                        request.getUser(),
                        request.getFriendNickname(),
                        request.getUserNickname(),
                        request.getRequestMessage()
                ))
                .collect(Collectors.toList());
    }


    // 건넨 일촌 신청 보기
    @Override
    @Transactional(readOnly = true)
    public List<GetIlchonResponseDto> getSentIlchonRequests(Integer currentUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        List<Ilchon> requests = ilchonRepository.findAllByUserAndStatus(currentUser, "PENDING");

        return requests.stream()
                .map(request -> new GetIlchonResponseDto(
                        request.getId(),
                        request.getFriend(),
                        request.getUserNickname(),
                        request.getFriendNickname(),
                        request.getRequestMessage()
                ))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public String getIlchonStatus(Integer currentUserId, Integer targetUserId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        User targetUser = entityFinder.getUserOrThrow(targetUserId);

        // 내가 보낸 신청 확인
        Optional<Ilchon> sentRequest = ilchonRepository.findByUserAndFriend(currentUser, targetUser);
        if (sentRequest.isPresent()) {
            return sentRequest.get().getStatus();
        }

        // 상대방이 보낸 신청 확인
        Optional<Ilchon> receivedRequest = ilchonRepository.findByUserAndFriend(targetUser, currentUser);
        return receivedRequest.map(ilchon -> ilchon.getStatus().equals("PENDING") ? "PENDING_RECEIVED" : ilchon.getStatus()).orElse("NONE");
    }
}
