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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;


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
        MiniHomepage miniHomepage = minihomeRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 미니홈피를 찾을 수 없습니다: " + userId));

        Page<Board> boardPage = boardRepository.findByMiniHomepageIdAndType(miniHomepage.getId(), type, pageable);

        return boardPage.map(GetBoardResponseDto::new);
    }

    @Override
    @Transactional
    public void createBoard(Integer writerId, CreateBoardRequestDto requestDto){
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("글을 작성할 사용자를 찾을 수 없습니다."));

        MiniHomepage targetHomepage = minihomeRepository.findByUserId(requestDto.getMinihomepageOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 작성할 미니홈피를 찾을 수 없습니다."));

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
    public void updateBoard(Integer boardId, Integer writerId, UpdateBoardRequestDto requestDto){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다." + boardId));

        if(!board.getUser().getId().equals(writerId)){
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }

        if(requestDto.getContent() != null){
            board.setContent(requestDto.getContent());
        }

        if(requestDto.getIsPublic() != null) {
            board.setPublic(requestDto.getIsPublic());
        }
    }

    @Override
    @Transactional
    public void deleteBoard(Integer boardId, Integer writerId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(writerId)){
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }

        if (!boardRepository.existsById(boardId)){
            throw new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다." + boardId);
        }
        boardRepository.deleteById(boardId);
    }
}
