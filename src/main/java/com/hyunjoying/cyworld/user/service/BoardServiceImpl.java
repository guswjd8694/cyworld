package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.user.entity.Board;
import com.hyunjoying.cyworld.user.entity.MiniHomepage;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.repository.BoardRepository;
import com.hyunjoying.cyworld.user.repository.MinihomeRepository;
import com.hyunjoying.cyworld.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MinihomeRepository minihomeRepository;


    @Override
    @Transactional
    public Page<GetBoardResponseDto> getBoards(Integer userId, String type, Pageable pageable){
        MiniHomepage miniHomepage = getMiniHomepageByUserIdOrThrow(userId);
        Page<Board> boardPage = boardRepository.findByMiniHomepageIdAndType(miniHomepage.getId(), type, pageable);

        return boardPage.map(GetBoardResponseDto::new);
    }


    @Override
    @Transactional
    public void createBoard(Integer writerId, CreateBoardRequestDto requestDto){
        User writer = getUserOrThrow(writerId);
        MiniHomepage targetHomepage = getMiniHomepageByUserIdOrThrow(requestDto.getMinihomepageOwnerId());

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
        Board board = getBoardOrThrow(boardId);
        User currentUser = getUserOrThrow(currentUserId);

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
        Board board = getBoardOrThrow(boardId);
        User currentUser = getUserOrThrow(currentUserId);

        board.checkDeletionPermission(currentUser);
        boardRepository.delete(board);
    }


    private User getUserOrThrow(Integer currentUserId){
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + currentUserId));
    }

    private Board getBoardOrThrow(Integer boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다: " + boardId));
    }

    private MiniHomepage getMiniHomepageByUserIdOrThrow(Integer userId) {
        return minihomeRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 미니홈피를 찾을 수 없습니다: " + userId));
    }
}
