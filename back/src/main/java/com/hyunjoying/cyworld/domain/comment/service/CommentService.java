package com.hyunjoying.cyworld.domain.comment.service;

import com.hyunjoying.cyworld.domain.comment.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.request.UpdateCommentRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.response.GetCommentResponseDto;

import java.util.List;

public interface CommentService {
    List<GetCommentResponseDto> getCommentsByBoardId(Integer boardId);
    GetCommentResponseDto createComment(Integer boardId, CreateCommentRequestDto requestDto, Integer writerId);
    void updateComment(Integer commentId, UpdateCommentRequestDto requestDto, Integer currentUserId);
    void deleteComment(Integer commentId, Integer currentUserId);
}
