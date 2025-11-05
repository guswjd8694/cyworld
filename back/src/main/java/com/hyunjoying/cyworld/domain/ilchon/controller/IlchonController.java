package com.hyunjoying.cyworld.domain.ilchon.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
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
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ilchonService.requestIlchon(userDetails.getUser().getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("일촌 신청을 보냈습니다."));
    }


    @Operation(summary = "일촌 신청 취소", description = "내가 보낸 일촌 신청을 취소합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 신청 취소 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
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
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ilchonService.acceptIlchon(userDetails.getUser().getId(), ilchonRequestId);
        return ResponseEntity.ok(new SuccessResponseDto("일촌을 수락했습니다."));
    }


    @Operation(summary = "일촌 거절", description = "받은 일촌 신청을 거절합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 신청 거절 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @DeleteMapping("/requests/{ilchonRequestId}/reject")
    public ResponseEntity<SuccessResponseDto> rejectIlchon(
            @PathVariable Integer ilchonRequestId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ilchonService.rejectIlchon(userDetails.getUser().getId(), ilchonRequestId);
        return ResponseEntity.ok(new SuccessResponseDto("일촌 신청을 거절했습니다."));
    }


    @Operation(summary = "일촌 끊기", description = "기존 일촌 관계를 끊습니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 끊기 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<SuccessResponseDto> breakIlchon(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ilchonService.breakIlchon(userDetails.getUser().getId(), targetUserId);
        return ResponseEntity.ok(new SuccessResponseDto("일촌 관계를 끊었습니다."));
    }


    @Operation(summary = "일촌 목록 조회", description = "특정 사용자의 일촌 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 목록 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetIlchonResponseDto.class)))
    )
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<GetIlchonResponseDto>> getIlchons(@PathVariable Integer userId) {
        List<GetIlchonResponseDto> ilchons = ilchonService.getIlchons(userId);
        return ResponseEntity.ok(ilchons);
    }


    @Operation(summary = "받은 일촌 신청 목록 조회", description = "내가 받은 일촌 신청 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "받은 신청 목록 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetIlchonRequestResponseDto.class)))
    )
    @GetMapping("/requests/received")
    public ResponseEntity<List<GetIlchonRequestResponseDto>> getReceivedIlchonRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<GetIlchonRequestResponseDto> requests = ilchonService.getReceivedIlchonRequests(userDetails.getUser().getId());
        return ResponseEntity.ok(requests);
    }


    @Operation(summary = "보낸 일촌 신청 목록 조회", description = "내가 보낸 일촌 신청 목록을 조회합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "보낸 신청 목록 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetIlchonResponseDto.class)))
    )
    @GetMapping("/requests/sent")
    public ResponseEntity<List<GetIlchonResponseDto>> getSentIlchonRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<GetIlchonResponseDto> requests = ilchonService.getSentIlchonRequests(userDetails.getUser().getId());
        return ResponseEntity.ok(requests);
    }


    @Operation(summary = "일촌 상태 조회", description = "나와 특정 사용자 간의 일촌 상태를 조회합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌 상태 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class, example = "{\"status\": \"ACCEPTED\"}"))
    )
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getIlchonStatus(
            @RequestParam Integer targetUserId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String status = ilchonService.getIlchonStatus(userDetails.getUser().getId(), targetUserId);
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "촌수 관계 계산", description = "두 사용자 간의 촌수 관계를 계산합니다.", tags = { "ilchon" })
    @ApiResponse(
            description = "촌수 관계 계산 성공. -1은 관계 없음을 의미합니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class, example = "{\"degree\": 1}"))
    )
    @GetMapping("/relationship")
    public ResponseEntity<Map<String, Integer>> getRelationship(
            @RequestParam Integer currentUserId,
            @RequestParam Integer targetUserId
    ) {
        Integer degree = ilchonService.calculateRelationshipDegree(currentUserId, targetUserId);

        Integer degreeValue = (degree == null) ? -1 : degree;

        return ResponseEntity.ok(Map.of("degree", degreeValue));
    }


    @Operation(summary = "일촌명 변경", description = "일촌명을 변경합니다.", tags = {"ilchon"})
    @ApiResponse(
            description = "일촌명 변경 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/nickname")
    public ResponseEntity<SuccessResponseDto> updateIlchonNickname(
            @RequestBody UpdateIlchonNicknameRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ilchonService.updateIlchonNickname(userDetails.getUser().getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("일촌명이 변경되었습니다."));
    }
}