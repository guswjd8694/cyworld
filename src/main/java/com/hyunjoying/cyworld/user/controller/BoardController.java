package com.hyunjoying.cyworld.user.controller;

import com.hyunjoying.cyworld.user.dto.SuccessResponseDto;
import com.hyunjoying.cyworld.user.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;


    @Operation(summary = "게시글 목록 조회", description = "특정 사용자의 게시글 목록을 조회합니다.", tags = { "board" })
    @ApiResponse(
            description = "게시글 목록 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<GetBoardResponseDto>> getBoards(
            @PathVariable Integer userId,
            @RequestParam String type,
            Pageable pageable
    ) {
        Page<GetBoardResponseDto> boardPage = boardService.getBoards(userId, type, pageable);

        return ResponseEntity.ok(boardPage);
    }


    @Operation(summary = "게시글 생성", description = "로그인한 사용자가 특정 미니홈피에 게시글을 작성합니다.", tags = { "board" })
    @ApiResponse(
            description = "게시글 생성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PostMapping("/users/{userId}")
    public ResponseEntity<SuccessResponseDto> createBoard(
            @PathVariable Integer userId,
            @RequestBody CreateBoardRequestDto requestDto,
            @AuthenticationPrincipal User writer
    ){
        System.out.println("DEBUG: @AuthenticationPrincipal로 주입된 writer: " + writer);
        if (writer != null) {
            System.out.println("DEBUG: writer의 ID: " + writer.getId());
            System.out.println("DEBUG: writer의 loginId: " + writer.getLoginId());
        }

        requestDto.setMinihomepageOwnerId(userId);
        assert writer != null;
        boardService.createBoard(writer.getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("게시글이 성공적으로 등록되었습니다."));
    }


    @Operation(summary = "게시글 수정", description = "로그인한 사용자가 자신의 게시글을 수정합니다.", tags = { "board" })
    @ApiResponse(
            description = "게시글 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @PutMapping("/{boardId}")
    public ResponseEntity<SuccessResponseDto> updateBoard(
            @PathVariable Integer boardId,
            @RequestBody UpdateBoardRequestDto requestDto,
            @AuthenticationPrincipal User writer
    ){
        boardService.updateBoard(boardId, writer.getId(), requestDto);
        return ResponseEntity.ok(new SuccessResponseDto("게시글이 성공적으로 수정되었습니다."));
    }


    @Operation(summary = "게시글 삭제", description = "로그인한 사용자가 자신의 게시글을 삭제합니다.", tags = { "board" })
    @ApiResponse(
            description = "게시글 삭제 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))
    )
    @DeleteMapping("/{boardId}")
    public ResponseEntity<SuccessResponseDto> deleteBoard(
            @PathVariable Integer boardId,
            @AuthenticationPrincipal User writer
    ){
        boardService.deleteBoard(boardId, writer.getId());
        return ResponseEntity.ok(new SuccessResponseDto("게시글이 성공적으로 삭제되었습니다."));
    }
}
