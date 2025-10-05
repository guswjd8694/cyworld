package com.hyunjoying.cyworld.domain.board.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.board.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.response.BoardCountDto;
import com.hyunjoying.cyworld.domain.board.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.board.entity.BoardImage;
import com.hyunjoying.cyworld.domain.board.repository.BoardImageRepository;
import com.hyunjoying.cyworld.domain.board.repository.BoardRepository;
import com.hyunjoying.cyworld.domain.comment.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.response.GetCommentResponseDto;
import com.hyunjoying.cyworld.domain.comment.entity.Comment;
import com.hyunjoying.cyworld.domain.comment.repository.CommentRepository;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.user.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.user.repository.IlchonRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final IlchonRepository ilchonRepository;
    private final EntityFinder entityFinder;
    private final BoardImageRepository boardImageRepository;

    @Override
    public Page<GetBoardResponseDto> getBoards(Integer userId, String type, Pageable pageable) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        Page<Board> boardPage;

        if ("ILCHONPYEONG".equals(type)) {
            List<Board> allIlchonpyeongs = boardRepository.findAllByMiniHomepageIdAndTypeOrderByCreatedAtDesc(miniHomepage.getId(), type);
            boardPage = new PageImpl<>(allIlchonpyeongs, Pageable.unpaged(), allIlchonpyeongs.size());
        } else {
            boardPage = boardRepository.findByMiniHomepageIdAndType(miniHomepage.getId(), type, pageable);
        }

        long totalElements = boardPage.getTotalElements();
        List<Board> boardList = boardPage.getContent();
        List<GetBoardResponseDto> dtoList = new ArrayList<>();

        for (int i = 0; i < boardList.size(); i++) {
            Board board = boardList.get(i);

            String nickname = null;
            if ("ILCHONPYEONG".equals(board.getType())) {
                if (!board.getUser().getId().equals(miniHomepage.getUser().getId())) {

                    nickname = ilchonRepository.findByUserAndFriendAndStatusAndIsActiveTrue(
                            miniHomepage.getUser(),
                            board.getUser(),
                            Ilchon.IlchonStatus.ACCEPTED)
                            .map(Ilchon::getFriendNickname)
                            .orElse(null);
                }
            }

            Long boardNo = null;
            if (!"ILCHONPYEONG".equals(type)) {
                boardNo = totalElements - ((long) pageable.getPageNumber() * pageable.getPageSize()) - i;
            }

            dtoList.add(new GetBoardResponseDto(board, board.getUser(), boardNo, nickname));
        }

        return new PageImpl<>(dtoList, pageable, totalElements);
    }

    @Override
    public List<GetBoardResponseDto> getRecentBoards(Integer userId) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        List<String> allowedTypes = Arrays.asList("PHOTO", "DIARY", "JUKEBOX");
        List<Board> recentBoards = boardRepository.findTop4ByMiniHomepageIdAndTypeInOrderByCreatedAtDesc(miniHomepage.getId(), allowedTypes);

        return recentBoards.stream()
                .map(board -> new GetBoardResponseDto(board, board.getUser(), null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createBoard(Integer writerId, CreateBoardRequestDto requestDto) {
        User writer = entityFinder.getUserOrThrow(writerId);
        MiniHomepage targetHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(requestDto.getMinihomepageOwnerId());
        String type = requestDto.getType();

        Board.BoardBuilder boardBuilder = Board.builder()
                .user(writer)
                .miniHomepage(targetHomepage)
                .type(type)
                .isPublic(requestDto.getPublicSetting());

        switch (type) {
            case "PHOTO":
                if (requestDto.getImageUrls() == null || requestDto.getImageUrls().isEmpty()) {
                    throw new IllegalArgumentException("이미지를 첨부해주세요.");
                }
                if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty()) {
                    throw new IllegalArgumentException("제목을 입력해주세요.");
                }

                Document doc = Jsoup.parse(requestDto.getContent());
                doc.select("img").remove();
                String contentWithoutImages = doc.body().html();

                boardBuilder.title(requestDto.getTitle()).content(contentWithoutImages);
                break;


            case "DIARY":
                boardBuilder.weather(requestDto.getWeather()).mood(requestDto.getMood()).content(requestDto.getContent());
                break;


            case "ILCHONPYEONG":
                boolean isOwner = writer.getId().equals(targetHomepage.getUser().getId());
                boolean isIlchon = ilchonRepository.findByUserAndFriendAndStatusAndIsActiveTrue(writer, targetHomepage.getUser(), Ilchon.IlchonStatus.ACCEPTED).isPresent();

                if (!isOwner && !isIlchon) {
                    throw new AccessDeniedException("일촌평은 일촌이거나 본인만 작성할 수 있습니다.");
                }
                boardBuilder.content(requestDto.getContent());
                break;

            default:
                boardBuilder.content(requestDto.getContent());
                break;
        }

        Board newBoard = boardBuilder.build();

        if ("PHOTO".equals(requestDto.getType())) {
            List<BoardImage> imageEntities = requestDto.getImageUrls().stream()
                    .map(url -> BoardImage.builder()
                            .board(newBoard)
                            .imageUrl(url)
                            .build())
                    .collect(Collectors.toList());

            newBoard.addImages(imageEntities);
        }

        boardRepository.save(newBoard);
    }


    @Override
    @Transactional
    public void updateBoard(Integer boardId, Integer currentUserId, UpdateBoardRequestDto requestDto) {
        Board originalBoard = entityFinder.getBoardOrThrow(boardId);
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        if (!originalBoard.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }

        boardRepository.delete(originalBoard);

        Board updatedBoard = Board.builder()
                .user(currentUser)
                .miniHomepage(originalBoard.getMiniHomepage())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .type(originalBoard.getType())
                .weather(requestDto.getWeather())
                .mood(requestDto.getMood())
                .isPublic(requestDto.getIsPublic())
                .originalBoardId(originalBoard.getOriginalBoardId())
                .version(originalBoard.getVersion() + 1)
                .build();

        if ("PHOTO".equals(updatedBoard.getType()) && requestDto.getImageUrls() != null) {
            List<BoardImage> imageEntities = requestDto.getImageUrls().stream()
                    .map(url -> BoardImage.builder().board(updatedBoard).imageUrl(url).build())
                    .toList();
            updatedBoard.addImages(imageEntities);
        }

        boardRepository.save(updatedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(Integer boardId, Integer currentUserId) {
        Board board = entityFinder.getBoardOrThrow(boardId);
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        board.checkDeletionPermission(currentUser);
        commentRepository.deleteAll(board.getComments());

        boardRepository.delete(board);
    }

    @Override
    @Transactional
    public void createComment(Integer boardId, Integer writerId, CreateCommentRequestDto requestDto) {
        Board board = entityFinder.getBoardOrThrow(boardId);
        User writer = entityFinder.getUserOrThrow(writerId);

        Comment comment = Comment.builder()
                .board(board)
                .user(writer)
                .content(requestDto.getContent())
                .build();

        commentRepository.save(comment);
    }

    @Override
    public List<GetCommentResponseDto> getComments(Integer boardId) {
        Board board = entityFinder.getBoardOrThrow(boardId);
        List<Comment> comments = commentRepository.findAllByBoardOrderByCreatedAtAsc(board);
        return comments.stream().map(GetCommentResponseDto::new).collect(Collectors.toList());
    }

    @Override
    public GetBoardResponseDto getDiaryByDate(Integer userId, LocalDate date) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return boardRepository.findByMiniHomepageIdAndTypeAndCreatedAtBetween(miniHomepage.getId(), "DIARY", startOfDay, endOfDay)
                .map(board -> new GetBoardResponseDto(board, board.getUser(), null, null))
                .orElse(null);
    }


    @Override
    @Transactional(readOnly = true)
    public Map<String, BoardCountDto> getBoardCounts(Integer userId) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        Map<String, BoardCountDto> counts = new HashMap<>();
        List<String> types = Arrays.asList("PHOTO", "DIARY", "JUKEBOX", "GUESTBOOK");

        LocalDateTime recentCount = LocalDateTime.now().minusDays((7));

        for (String type : types) {
            long totalCount = boardRepository.countByMiniHomepageIdAndType(miniHomepage.getId(), type);
            long newCount = boardRepository.countByMiniHomepageIdAndTypeAndCreatedAtAfter(miniHomepage.getId(), type, recentCount);
            counts.put(type, new BoardCountDto(newCount, totalCount));
        }

        return counts;
    }
}

