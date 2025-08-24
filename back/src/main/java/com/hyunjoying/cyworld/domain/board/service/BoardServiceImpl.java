package com.hyunjoying.cyworld.domain.board.service;

import com.hyunjoying.cyworld.domain.board.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.domain.comment.dto.response.GetCommentResponseDto;
import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.comment.entity.Comment;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.user.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.board.repository.BoardRepository;
import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.comment.repository.CommentRepository;
import com.hyunjoying.cyworld.domain.user.repository.IlchonRepository;
import com.hyunjoying.cyworld.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final EntityFinder entityFinder;
    private final CommentRepository commentRepository;
    private final IlchonRepository ilchonRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public Page<GetBoardResponseDto> getBoards(Integer userId, String type, Pageable pageable){
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        Page<Board> boardPage = boardRepository.findByMiniHomepageIdAndType(miniHomepage.getId(), type, pageable);

        if ("ILCHONPYEONG".equals(type)) {
            List<Board> allIlchonpyeongs = boardRepository.findAllByMiniHomepageIdAndType(miniHomepage.getId(), type);
            boardPage = new PageImpl<>(allIlchonpyeongs, Pageable.unpaged(), allIlchonpyeongs.size());
        } else {
            boardPage = boardRepository.findByMiniHomepageIdAndType(miniHomepage.getId(), type, pageable);
        }


        long totalElements = boardPage.getTotalElements();

        List<GetBoardResponseDto> dtoList = new ArrayList<>();
        List<Board> boardList = boardPage.getContent();

        for (int i = 0; i < boardList.size(); i++) {
            Board board = boardList.get(i);
            Long boardNo = null;
            String nickname = null;

            if ("ILCHONPYEONG".equals(board.getType())) {
                nickname = ilchonRepository.findByUserAndFriend(board.getUser(), miniHomepage.getUser())
                        .map(Ilchon::getUserNickname)
                        .orElse("일촌명 없음");
            } else {
                boardNo = totalElements - ((long) pageable.getPageNumber() * pageable.getPageSize()) - i;
            }

            dtoList.add(new GetBoardResponseDto(board, boardNo, nickname));
        }

        return new PageImpl<>(dtoList, pageable, totalElements);
    }


    @Override
    @Transactional
    public void createBoard(Integer writerId, CreateBoardRequestDto requestDto){
        User writer = entityFinder.getUserOrThrow(writerId);
        MiniHomepage targetHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(requestDto.getMinihomepageOwnerId());

        if ("ILCHONPYEONG".equals(requestDto.getType())) {
            boolean isOwner = writer.getId().equals(targetHomepage.getUser().getId());

            boolean isIlchon = ilchonRepository.findByUserAndFriend(writer, targetHomepage.getUser())
                    .map(ilchon -> "ACCEPTED".equals(ilchon.getStatus()))
                    .orElse(false);

            if (!isOwner && !isIlchon) {
                throw new AccessDeniedException("일촌평은 일촌이거나 본인만 작성할 수 있습니다.");
            }
        }

        Board newBoard = new Board();
        newBoard.setUser(writer);
        newBoard.setMiniHomepage(targetHomepage);
        newBoard.setContent(requestDto.getContent());
        newBoard.setType(requestDto.getType());
        newBoard.setWeather(requestDto.getWeather());
        newBoard.setMood(requestDto.getMood());
        newBoard.setCreatedBy(writerId);

        if (requestDto.getPublicSetting() != null) {
            newBoard.setPublic(requestDto.getPublicSetting());
        } else {
            newBoard.setPublic(true);
        }

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

    @Override
    @Transactional(readOnly = true)
    public GetBoardResponseDto getDiaryByDate(Integer userId, LocalDate date) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return boardRepository.findByMiniHomepageIdAndTypeAndIsDeletedFalseAndCreatedAtBetween(miniHomepage.getId(), "DIARY", startOfDay, endOfDay)
                .map(board -> new GetBoardResponseDto(board, null, null))
                .orElse(null);
    }
}
