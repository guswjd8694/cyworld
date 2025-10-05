package com.hyunjoying.cyworld.domain.comment.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.comment.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.request.UpdateCommentRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.response.GetCommentResponseDto;
import com.hyunjoying.cyworld.domain.comment.entity.Comment;
import com.hyunjoying.cyworld.domain.comment.repository.CommentRepository;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EntityFinder entityFinder;

    @Override
    @Transactional
    public List<GetCommentResponseDto> getCommentsByBoardId(Integer boardId) {
        Board board = entityFinder.getBoardOrThrow(boardId);

        return commentRepository.findAllByBoardOrderByCreatedAtAsc(board)
                .stream()
                .map(GetCommentResponseDto::new)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public GetCommentResponseDto createComment(Integer boardId, CreateCommentRequestDto requestDto, Integer writerId) {
        Board board = entityFinder.getBoardOrThrow(boardId);
        User writer = entityFinder.getUserOrThrow(writerId);

        Comment newComment = Comment.builder()
                .board(board)
                .user(writer)
                .content(requestDto.getContent())
                .build();

        Comment savedComment = commentRepository.save(newComment);
        return new GetCommentResponseDto(savedComment);
    }


    @Override
    @Transactional
    public void updateComment(Integer commentId, UpdateCommentRequestDto requestDto, Integer currentUserId) {
        Comment originalComment = entityFinder.getCommentOrThrow(commentId);
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        if (!originalComment.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("댓글을 수정할 권한이 없습니다.");
        }

        commentRepository.delete(originalComment);

        Comment updatedComment = Comment.builder()
                .board(originalComment.getBoard())
                .user(currentUser)
                .content(requestDto.getContent())
                .originalCommentId(originalComment.getOriginalCommentId())
                .version(originalComment.getVersion() + 1)
                .build();

        commentRepository.save(updatedComment);
    }


    @Override
    @Transactional
    public void deleteComment(Integer commentId, Integer currentUserId) {
        Comment comment = commentRepository.findWithDetailsById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        boolean isAuthor = comment.getUser().getId().equals(currentUser.getId());
        boolean isBoardOwner = comment.getBoard().getMiniHomepage().getUser().getId().equals(currentUser.getId());

        if (!isAuthor && !isBoardOwner) {
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
