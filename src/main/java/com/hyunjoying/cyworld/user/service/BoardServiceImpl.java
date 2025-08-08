package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.user.entity.Board;
import com.hyunjoying.cyworld.user.entity.MiniHomepage;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.repository.BoardRepository;
import com.hyunjoying.cyworld.common.util.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final EntityFinder entityFinder;


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
}
