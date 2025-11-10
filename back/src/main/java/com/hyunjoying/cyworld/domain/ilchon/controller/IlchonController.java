package com.hyunjoying.cyworld.domain.ilchon.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.UpdateIlchonRequestStatusDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonRelationshipResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.service.IlchonRequestService;
import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.RequestIlchonDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.request.UpdateIlchonNicknameRequestDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonRequestResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.dto.response.GetIlchonResponseDto;
import com.hyunjoying.cyworld.domain.ilchon.service.IlchonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class IlchonController {
    private final IlchonService ilchonService;
    private final IlchonRequestService ilchonRequestService;


    @Operation(summary = "일촌 신청", description = "다른 사용자에게 일촌을 신청합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 신청 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/ilchons-requests")
    public ResponseEntity<SuccessResponseDto> requestIlchon(
            @RequestBody RequestIlchonDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ilchonRequestService.createIlchonRequest(userDetails.getUser().getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("일촌 신청을 보냈습니다."));
    }


    @Operation(summary = "일촌 신청 취소", description = "내가 보낸 일촌 신청을 취소합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 신청 취소 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @DeleteMapping("/ilchons-requests/{ilchonRequestId}")
    public ResponseEntity<SuccessResponseDto> cancelIlchonRequest(
            @PathVariable Integer ilchonRequestId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ilchonRequestService.cancelIlchonRequest(userDetails.getUser().getId(), ilchonRequestId);
        return ResponseEntity.ok(new SuccessResponseDto("일촌 신청을 취소했습니다."));
    }


    @Operation(summary = "일촌 신청 상태 변경 (수락/거절)", description = "받은 일촌 신청을 수락하거나 거절합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 신청 상태 변경 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PatchMapping("/ilchons-requests/{ilchonRequestId}")
    public ResponseEntity<SuccessResponseDto> updateIlchonRequestStatus(
            @PathVariable Integer ilchonRequestId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpdateIlchonRequestStatusDto requestDto
    ) {
        ilchonRequestService.updateIlchonRequestStatus(
                userDetails.getUser().getId(),
                ilchonRequestId,
                requestDto
        );

        String message = "ACCEPTED".equalsIgnoreCase(requestDto.getStatus())
                ? "일촌 신청을 수락했습니다."
                : "일촌 신청을 거절했습니다.";

        return ResponseEntity.ok(new SuccessResponseDto(message));
    }



    @Operation(summary = "일촌 끊기", description = "기존 일촌 관계를 끊습니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 끊기 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @DeleteMapping("/ilchons/{targetUserId}")
    public ResponseEntity<SuccessResponseDto> breakIlchon(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ilchonService.breakIlchon(userDetails.getUser().getId(), targetUserId);
        return ResponseEntity.ok(new SuccessResponseDto("일촌 관계를 끊었습니다."));
    }



    @Operation(summary = "받은 일촌 신청 목록 조회", description = "내가 받은 일촌 신청 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "받은 신청 목록 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetIlchonRequestResponseDto.class)))
    )
    @GetMapping(value = "/ilchons-requests", params = "toUser=me")
    public ResponseEntity<List<GetIlchonRequestResponseDto>> getReceivedIlchonRequests(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "status", defaultValue = "PENDING") String status
    ) {
        List<GetIlchonRequestResponseDto> requests = ilchonRequestService.getReceivedIlchonRequests(userDetails.getUser().getId(), status);
        return ResponseEntity.ok(requests);
    }


    @Operation(summary = "보낸 일촌 신청 목록 조회", description = "내가 보낸 일촌 신청 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "보낸 신청 목록 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetIlchonRequestResponseDto.class)))
    )
    @GetMapping(value = "/ilchons-requests", params = "fromUser=me")
    public ResponseEntity<List<GetIlchonRequestResponseDto>> getSentIlchonRequests(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "status", defaultValue = "PENDING") String status
    ) {
        List<GetIlchonRequestResponseDto> requests = ilchonRequestService.getSentIlchonRequests(userDetails.getUser().getId(), status);
        return ResponseEntity.ok(requests);
    }



    @Operation(summary = "촌수 및 관계 조회", description = "두 사용자 간의 촌수와 닉네임을 조회합니다.", tags = { "ilchon" })
    @ApiResponse(
            description = "촌수 관계 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetIlchonRelationshipResponseDto.class))
    )
    @GetMapping("/ilchons")
    public ResponseEntity<GetIlchonRelationshipResponseDto> getRelationship(
             @RequestParam Integer currentUserId,
             @RequestParam Integer targetUserId
    ) {
        GetIlchonRelationshipResponseDto responseDto = ilchonService.getRelationship(currentUserId, targetUserId);

        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "일촌명 변경", description = "일촌명을 변경합니다. (내가 상대를 부르는 일촌명)", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌명 변경 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/ilchons/nickname")
    public ResponseEntity<SuccessResponseDto> updateIlchonNickname(
            @RequestBody UpdateIlchonNicknameRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ilchonService.updateIlchonNickname(userDetails.getUser().getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("일촌명이 변경되었습니다."));
    }
}