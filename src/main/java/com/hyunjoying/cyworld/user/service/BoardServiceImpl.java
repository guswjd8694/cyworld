package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.user.dto.response.GetCommentResponseDto;
import com.hyunjoying.cyworld.user.entity.Board;
import com.hyunjoying.cyworld.user.entity.Comment;
import com.hyunjoying.cyworld.user.entity.MiniHomepage;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.repository.BoardRepository;
import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.user.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final EntityFinder entityFinder;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public Page<GetBoardResponseDto> getBoards(Integer userId, String type, Pageable pageable){
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        Page<Board> boardPage = boardRepository.findByMiniHomepageIdAndType(miniHomepage.getId(), type, pageable);

        return boardPage.map(GetBoardResponseDto::new);
    }


    @Override
    @Transactional
    public void createBoard(Integer writerId, CreateBoardRequestDto requestDto){
        User writer = entityFinder.getUserOrThrow(writerId);
        MiniHomepage targetHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(requestDto.getMinihomepageOwnerId());

        Board newBoard = new Board();
        newBoard.setUser(writer);
        newBoard.setMiniHomepage(targetHomepage);
        newBoard.setContent(requestDto.getContent());
        newBoard.setType(requestDto.getType());
        newBoard.setPublic(requestDto.isPublic());
        newBoard.setCreatedBy(writerId);

        boardRepository.save(newBoard);
    }


    @Override
    @Transactional
    public void updateBoard(Integer boardId, Integer currentUserId, UpdateBoardRequestDto requestDto){
        Board board = entityFinder.getBoardOrThrow(boardId);
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        if (requestDto.getContent() != null){
            board.updateContent(requestDto.getContent(), currentUser);
        }

        if (requestDto.getIsPublic() != null){
            board.updatePrivacy(requestDto.getIsPublic(), currentUser);
        }
    }


    @Override
    @Transactional
    public void deleteBoard(Integer boardId, Integer currentUserId){
        Board board = entityFinder.getBoardOrThrow(boardId);
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        board.checkDeletionPermission(currentUser);
        boardRepository.delete(board);
    }


    @Override
    @Transactional
    public void createComment(Integer boardId, Integer writerId, CreateCommentRequestDto requestDto){
        Board board = entityFinder.getBoardOrThrow(boardId);
        User writer = entityFinder.getUserOrThrow(writerId);

        Comment newComment = new Comment();
        newComment.setBoard(board);
        newComment.setUser(writer);
        newComment.setContent(requestDto.getContent());
        newComment.setCreatedBy(writer.getId());

        commentRepository.save(newComment);
    }


    @Override
    @Transactional
    public List<GetCommentResponseDto> getComments(Integer boardId) {
        Board board = entityFinder.getBoardOrThrow(boardId);
        List<Comment> comments = commentRepository.findAllByBoardOrderByCreatedAtAsc(board);

        return comments.stream().map(GetCommentResponseDto::new).collect(Collectors.toList());
    }
}
