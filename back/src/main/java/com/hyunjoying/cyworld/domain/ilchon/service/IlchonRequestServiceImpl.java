package com.hyunjoying.cyworld.domain.ilchon.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.RequestIlchonDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.UpdateIlchonRequestStatusDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonRequestResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
import com.hyunjoying.cyworld.domain.ilchon.entity.IlchonRequest;
import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRepository;
import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRequestRepository;
import com.hyunjoying.cyworld.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IlchonRequestServiceImpl implements IlchonRequestService {

    private final IlchonRequestRepository ilchonRequestRepository;
    private final IlchonRepository ilchonRepository;
    private final EntityFinder entityFinder;

    @Override
    @Transactional
    public void createIlchonRequest(Integer currentUserId, RequestIlchonDto requestDto) {
        User fromUser = entityFinder.getUserOrThrow(currentUserId);
        User toUser = entityFinder.getUserOrThrow(requestDto.getTargetUserId());

        if (fromUser.equals(toUser)) {
            throw new IllegalArgumentException("자기 자신에게 일촌을 신청할 수 없습니다.");
        }

        ilchonRequestRepository.findExistingPendingRequest(fromUser, toUser, IlchonRequest.IlchonRequestStatus.PENDING)
                .ifPresent(req -> {
                    throw new IllegalArgumentException("이미 일촌 신청이 진행 중입니다.");
                });

        ilchonRepository.findByUserAndFriendAndStatus(fromUser, toUser, Ilchon.IlchonStatus.ACCEPTED)
                .ifPresent(ilchon -> {
                    throw new IllegalArgumentException("이미 일촌 관계입니다.");
                });

        IlchonRequest newRequest = IlchonRequest.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .nicknameForFromUser(requestDto.getNicknameForFromUser())
                .nicknameForToUser(requestDto.getNicknameForToUser())
                .requestMessage(requestDto.getRequestMessage())
                .status(IlchonRequest.IlchonRequestStatus.PENDING)
                .build();

        ilchonRequestRepository.save(newRequest);
    }

    @Override
    @Transactional
    public void updateIlchonRequestStatus(Integer currentUserId, Integer ilchonRequestId, UpdateIlchonRequestStatusDto requestDto) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        IlchonRequest request = entityFinder.getIlchonRequestOrThrow(ilchonRequestId);

        if (!request.getToUser().equals(currentUser)) {
            throw new AccessDeniedException("일촌 신청을 처리할 권한이 없습니다.");
        }

        String statusStr = requestDto.getStatus();
        if ("ACCEPTED".equalsIgnoreCase(statusStr)) {

            request.setStatus(IlchonRequest.IlchonRequestStatus.ACCEPTED);

            User fromUser = request.getFromUser();
            User toUser = request.getToUser();

            Ilchon ilchonForFrom = Ilchon.builder()
                    .user(fromUser)
                    .friend(toUser)
                    .nickname(request.getNicknameForToUser())
                    .status(Ilchon.IlchonStatus.ACCEPTED)
                    .build();

            Ilchon ilchonForTo = Ilchon.builder()
                    .user(toUser)
                    .friend(fromUser)
                    .nickname(request.getNicknameForFromUser())
                    .status(Ilchon.IlchonStatus.ACCEPTED)
                    .build();

            ilchonRepository.saveAll(List.of(ilchonForFrom, ilchonForTo));

        } else if ("REJECTED".equalsIgnoreCase(statusStr)) {
            request.setStatus(IlchonRequest.IlchonRequestStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("status는 ACCEPTED 또는 REJECTED여야 합니다.");
        }
    }

    @Override
    @Transactional
    public void cancelIlchonRequest(Integer currentUserId, Integer ilchonRequestId) {
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        IlchonRequest request = entityFinder.getIlchonRequestOrThrow(ilchonRequestId);

        if (!request.getFromUser().equals(currentUser)) {
            throw new AccessDeniedException("일촌 신청을 취소할 권한이 없습니다.");
        }

        request.setStatus(IlchonRequest.IlchonRequestStatus.CANCELED);
    }

    @Override
    public List<GetIlchonRequestResponseDto> getReceivedIlchonRequests(Integer currentUserId, String status) {
        User toUser = entityFinder.getUserOrThrow(currentUserId);
        IlchonRequest.IlchonRequestStatus reqStatus = IlchonRequest.IlchonRequestStatus.valueOf(status.toUpperCase());

        List<IlchonRequest> requests = ilchonRequestRepository.findAllByToUserAndStatus(toUser, reqStatus);

        return requests.stream()
                .map(GetIlchonRequestResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetIlchonRequestResponseDto> getSentIlchonRequests(Integer currentUserId, String status) {
        User fromUser = entityFinder.getUserOrThrow(currentUserId);
        IlchonRequest.IlchonRequestStatus reqStatus = IlchonRequest.IlchonRequestStatus.valueOf(status.toUpperCase());

        List<IlchonRequest> requests = ilchonRequestRepository.findAllByFromUserAndStatus(fromUser, reqStatus);

        return requests.stream()
                .map(GetIlchonRequestResponseDto::new)
                .collect(Collectors.toList());
    }
}