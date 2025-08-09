package com.hyunjoying.cyworld.domain.user.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import com.hyunjoying.cyworld.domain.user.dto.request.RequestIlchonDto;
import com.hyunjoying.cyworld.domain.user.dto.response.GetIlchonResponseDto;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.user.service.IlchonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ilchons")
@RequiredArgsConstructor
public class IlchonController {
    private final IlchonService ilchonService;


    @Operation(summary = "일촌 신청", description = "다른 사용자에게 일촌을 신청합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 신청 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping
    public ResponseEntity<SuccessResponseDto> requestIlchon(
            @RequestBody RequestIlchonDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ilchonService.requestIlchon(((UserDetailsImpl) userDetails).getUser().getId(), requestDto);

        return ResponseEntity.ok(new SuccessResponseDto("일촌 신청을 보냈습니다."));
    }


    @Operation(summary = "일촌 신청 취소", description = "내가 보낸 일촌 신청을 취소합니다.", tags = {"ilchon"})
    @DeleteMapping("/requests/{ilchonRequestId}/cancel")
    public ResponseEntity<SuccessResponseDto> cancelIlchonRequest(
            @PathVariable Integer ilchonRequestId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ilchonService.cancelIlchonRequest(userDetails.getUser().getId(), ilchonRequestId);

        return ResponseEntity.ok(new SuccessResponseDto("일촌 신청을 취소했습니다."));
    }


    @Operation(summary = "일촌 수락", description = "받은 일촌 신청을 수락합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 수락 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/{ilchonRequestId}/accept")
    public ResponseEntity<SuccessResponseDto> acceptIlchon(
            @PathVariable Integer ilchonRequestId,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        ilchonService.acceptIlchon(((UserDetailsImpl) userDetails).getUser().getId(), ilchonRequestId);

        return ResponseEntity.ok(new SuccessResponseDto("일촌을 수락했습니다."));
    }


    @Operation(summary = "일촌 거절", description = "받은 일촌 신청을 거절합니다.", tags = {"ilchon"})
    @DeleteMapping("/requests/{ilchonRequestId}/reject")
    public ResponseEntity<SuccessResponseDto> rejectIlchon(
            @PathVariable Integer ilchonRequestId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ilchonService.rejectIlchon(userDetails.getUser().getId(), ilchonRequestId);

        return ResponseEntity.ok(new SuccessResponseDto("일촌 신청을 거절했습니다."));
    }


    @Operation(summary = "일촌 끊기", description = "기존 일촌 관계를 끊습니다.", tags = {"ilchon"})
    @ApiResponse(description = "일촌 끊기 성공")
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<SuccessResponseDto> breakIlchon(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ilchonService.breakIlchon(userDetails.getUser().getId(), targetUserId);

        return ResponseEntity.ok(new SuccessResponseDto("일촌 관계를 끊었습니다."));
    }


    @Operation(summary = "일촌 목록 조회", description = "특정 사용자의 일촌 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(description = "일촌 목록 조회 성공")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<GetIlchonResponseDto>> getIlchons(@PathVariable Integer userId) {
        List<GetIlchonResponseDto> ilchons = ilchonService.getIlchon(userId);

        return ResponseEntity.ok(ilchons);
    }


    @Operation(summary = "받은 일촌 신청 목록 조회", description = "내가 받은 일촌 신청 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(description = "받은 신청 목록 조회 성공")
    @GetMapping("/requests/received")
    public ResponseEntity<List<GetIlchonResponseDto>> getReceivedIlchonRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User currentUser = userDetails.getUser();
        List<GetIlchonResponseDto> requests = ilchonService.getReceivedIlchonRequests(currentUser.getId());

        return ResponseEntity.ok(requests);
    }


    @Operation(summary = "보낸 일촌 신청 목록 조회", description = "내가 보낸 일촌 신청 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(description = "보낸 신청 목록 조회 성공")
    @GetMapping("/requests/sent")
    public ResponseEntity<List<GetIlchonResponseDto>> getSentIlchonRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<GetIlchonResponseDto> requests = ilchonService.getSentIlchonRequests(userDetails.getUser().getId());

        return ResponseEntity.ok(requests);
    }
}
