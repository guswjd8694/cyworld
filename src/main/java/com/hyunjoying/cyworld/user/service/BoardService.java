package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.user.dto.response.GetCommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {
    Page<GetBoardResponseDto> getBoards(Integer userId, String type, Pageable pageable);

    void createBoard(Integer userId, CreateBoardRequestDto createBoardRequestDto);

    void updateBoard(Integer boardId, Integer writerId, UpdateBoardRequestDto updateBoardRequestDto);

    void deleteBoard(Integer boardId, Integer writerId);

    void createComment(Integer boardId, Integer writerId, CreateCommentRequestDto requestDto);

    List<GetCommentResponseDto> getComments(Integer boardId);
}
