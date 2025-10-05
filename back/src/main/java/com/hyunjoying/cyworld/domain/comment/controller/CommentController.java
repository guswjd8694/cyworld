package com.hyunjoying.cyworld.domain.comment.controller;

import com.hyunjoying.cyworld.common.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.domain.comment.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.request.UpdateCommentRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.response.GetCommentResponseDto;
import com.hyunjoying.cyworld.domain.comment.service.CommentService;
import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 모든 댓글을 조회합니다.", tags = {"comment"})
    @ApiResponse(
            description = "댓글 목록 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetCommentResponseDto.class))
    )
    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<List<GetCommentResponseDto>> getComments(@PathVariable Integer boardId) {
        List<GetCommentResponseDto> comments = commentService.getCommentsByBoardId(boardId);

        return ResponseEntity.ok(comments);
    }



    @Operation(summary = "댓글 생성", description = "특정 게시글에 댓글을 작성합니다.", tags = {"comment"})
    @ApiResponse(
            description = "댓글 생성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<GetCommentResponseDto> createComment(
            @PathVariable Integer boardId,
            @RequestBody CreateCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Integer writerId = userDetails.getUser().getId();
        GetCommentResponseDto newComment = commentService.createComment(boardId, requestDto, writerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }


    @Operation(summary = "댓글 수정", description = "특정 게시글의 댓글을 수정합니다.", tags = {"comment"})
    @ApiResponse(
            description = "댓글 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<SuccessResponseDto> updateComment(
            @PathVariable Integer commentId,
            @RequestBody UpdateCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.updateComment(commentId, requestDto, userDetails.getUser().getId());
        return ResponseEntity.ok(new SuccessResponseDto("댓글이 수정되었습니다."));
    }


    @Operation(summary = "댓글 삭제", description = "특정 게시글의 댓글을 삭제합니다.", tags = {"comment"})
    @ApiResponse(
            description = "댓글 삭제 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<SuccessResponseDto> deleteComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.deleteComment(commentId, userDetails.getUser().getId());
        return ResponseEntity.ok(new SuccessResponseDto("댓글이 삭제되었습니다."));
    }
}
