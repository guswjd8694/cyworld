package com.hyunjoying.cyworld.domain.board.controller;

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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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


    @Operation(summary = "댓글 생성", description = "특정 게시글에 댓글을 작성합니다.", tags = {"comment"})
    @ApiResponse(
            description = "댓글 생성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<SuccessResponseDto> createComment(
            @PathVariable Integer boardId,
            @RequestBody CreateCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User writer = userDetails.getUser();

        boardService.createComment(boardId, writer.getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("댓글이 성공적으로 작성되었습니다."));
    }


    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 모든 댓글을 조회합니다.", tags = {"comment"})
    @ApiResponse(
            description = "댓글 목록 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetCommentResponseDto.class))
    )
    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<List<GetCommentResponseDto>> getComments(
            @PathVariable Integer boardId
    ){
        List<GetCommentResponseDto> comments = boardService.getComments(boardId);
        return ResponseEntity.ok(comments);
    }


    @GetMapping("/users/{userId}/diary")
    public ResponseEntity<GetBoardResponseDto> getDiaryByDate(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        GetBoardResponseDto diary = boardService.getDiaryByDate(userId, date);
        return ResponseEntity.ok(diary);
    }
}
