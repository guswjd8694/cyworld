package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetBoardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    Page<GetBoardResponseDto> getBoards(Integer userId, String type, Pageable pageable);

    void createBoard(Integer userId, CreateBoardRequestDto createBoardRequestDto);

    void updateBoard(Integer boardId, Integer writerId, UpdateBoardRequestDto updateBoardRequestDto);

    void deleteBoard(Integer boardId, Integer writerId);
}
