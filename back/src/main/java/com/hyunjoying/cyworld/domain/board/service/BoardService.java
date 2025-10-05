package com.hyunjoying.cyworld.domain.board.service;

import com.hyunjoying.cyworld.domain.board.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.response.BoardCountDto;
import com.hyunjoying.cyworld.domain.comment.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.domain.comment.dto.response.GetCommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BoardService {
    Page<GetBoardResponseDto> getBoards(Integer userId, String type, Pageable pageable);

    void createBoard(Integer userId, CreateBoardRequestDto createBoardRequestDto);

    void updateBoard(Integer boardId, Integer writerId, UpdateBoardRequestDto updateBoardRequestDto);

    void deleteBoard(Integer boardId, Integer writerId);

    void createComment(Integer boardId, Integer writerId, CreateCommentRequestDto requestDto);

    List<GetCommentResponseDto> getComments(Integer boardId);

    GetBoardResponseDto getDiaryByDate(Integer userId, LocalDate date);

    List<GetBoardResponseDto> getRecentBoards(Integer userId);

    Map<String, BoardCountDto> getBoardCounts(Integer userId);
}
