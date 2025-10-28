package com.hyunjoying.cyworld.domain.board.controller;

import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardPrivacyDto;
import com.hyunjoying.cyworld.domain.board.dto.response.BoardCountDto;
import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.board.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.domain.comment.dto.response.GetCommentResponseDto;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;


    @Operation(summary = "게시글 목록 조회", description = "특정 사용자의 게시글 목록을 조회합니다.", tags = {"board"})
    @ApiResponse(
            description = "게시글 목록 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/users/{userId}/boards")
    public ResponseEntity<Page<GetBoardResponseDto>> getBoards(
            @PathVariable Integer userId,
            @RequestParam String type,
            Pageable pageable
    ) {
        Page<GetBoardResponseDto> boardPage = boardService.getBoards(userId, type, pageable);

        return ResponseEntity.ok(boardPage);
    }


    @Operation(summary = "게시글 생성", description = "로그인한 사용자가 특정 미니홈피에 게시글을 작성합니다.", tags = {"board"})
    @ApiResponse(
            description = "게시글 생성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/users/{userId}/boards")
    public ResponseEntity<SuccessResponseDto> createBoard(
            @PathVariable Integer userId,
            @RequestBody CreateBoardRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User writer = userDetails.getUser();

        requestDto.setMinihomepageOwnerId(userId);
        boardService.createBoard(writer.getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("게시글이 성공적으로 등록되었습니다."));
    }


    @Operation(summary = "게시글 수정", description = "로그인한 사용자가 자신의 게시글을 수정합니다.", tags = {"board"})
    @ApiResponse(
            description = "게시글 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<SuccessResponseDto> updateBoard(
            @PathVariable Integer boardId,
            @RequestBody UpdateBoardRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User writer = userDetails.getUser();

        boardService.updateBoard(boardId, writer.getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("게시글이 성공적으로 수정되었습니다."));
    }

//    @PutMapping("/api/boards/{boardId}/privacy")
//    public void updateBoardPrivacy(
//            @PathVariable Integer boardId,
//            @Valid @RequestBody UpdateBoardPrivacyDto requestDto,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
//        boardService.updateBoardPrivacy(boardId, requestDto.getIsPublic(), userDetails.getUser());
//    }


    @Operation(summary = "게시글 삭제", description = "로그인한 사용자가 자신의 게시글을 삭제합니다.", tags = {"board"})
    @ApiResponse(
            description = "게시글 삭제 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<SuccessResponseDto> deleteBoard(
            @PathVariable Integer boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User writer = userDetails.getUser();

        boardService.deleteBoard(boardId, writer.getId());
        return ResponseEntity.ok(new SuccessResponseDto("게시글이 성공적으로 삭제되었습니다."));
    }


    @Operation(summary = "날짜별 다이어리 조회", description = "특정 사용자의 특정 날짜에 작성된 다이어리를 조회합니다.", tags = {"board"})
    @ApiResponse(
            description = "다이어리 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetBoardResponseDto.class))
    )
    @GetMapping("/users/{userId}/diary")
    public ResponseEntity<GetBoardResponseDto> getDiaryByDate(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        GetBoardResponseDto diary = boardService.getDiaryByDate(userId, date);
        return ResponseEntity.ok(diary);
    }


    @Operation(summary = "최근 게시물 조회", description = "홈 화면에 표시될 최근 게시물 4개를 조회합니다.", tags = {"board"})
    @ApiResponse(
            description = "최근 게시물 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetBoardResponseDto.class)))
    )
    @GetMapping("/users/{userId}/boards/recent")
    public ResponseEntity<List<GetBoardResponseDto>> getRecentBoards(@PathVariable Integer userId) {
        List<GetBoardResponseDto> recentBoards = boardService.getRecentBoards(userId);
        return ResponseEntity.ok(recentBoards);
    }


    @Operation(summary = "게시판별 게시물 수 조회", description = "사진첩, 다이어리, 방명록 각각의 게시물 수를 조회합니다.", tags = {"board"})
    @ApiResponse(
            description = "게시판별 게시물 수 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    @GetMapping("/users/{userId}/board-counts")
    public ResponseEntity<Map<String, BoardCountDto>> getBoardCounts(@PathVariable Integer userId) {
        Map<String, BoardCountDto> counts = boardService.getBoardCounts(userId);
        return ResponseEntity.ok(counts);
    }
}