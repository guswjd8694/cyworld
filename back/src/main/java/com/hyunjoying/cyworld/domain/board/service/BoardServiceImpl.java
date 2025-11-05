package com.hyunjoying.cyworld.domain.board.service;

import com.hyunjoying.cyworld.common.exception.ValidationExceptionHandler;
import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.board.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardPrivacyDto;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.response.BoardCountDto;
import com.hyunjoying.cyworld.domain.board.dto.response.GetBoardResponseDto;
import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.board.entity.BoardImage;
import com.hyunjoying.cyworld.domain.board.repository.BoardRepository;
import com.hyunjoying.cyworld.domain.comment.dto.request.CreateCommentRequestDto;
import com.hyunjoying.cyworld.domain.comment.dto.response.GetCommentResponseDto;
import com.hyunjoying.cyworld.domain.comment.entity.Comment;
import com.hyunjoying.cyworld.domain.comment.repository.CommentRepository;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
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
    private final EntityManager entityManager;


    @Override
    public Page<GetBoardResponseDto> getBoards(Integer userId, User viewer, String type, LocalDate date, Pageable pageable) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        Page<Board> boardPage;

        boolean isOwner = (viewer != null) && viewer.getId().equals(userId);
        Integer viewerId = (viewer != null) ? viewer.getId() : null;

        if (date != null) {
            if (!"DIARY".equals(type)) {
                throw new IllegalArgumentException("날짜별 조회는 다이어리만 가능합니다.");
            }

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            List<Board> boards;

            if (isOwner) {
                boards = boardRepository.findByMiniHomepageIdAndTypeAndCreatedAtBetween(miniHomepage.getId(), "DIARY", startOfDay, endOfDay);
            } else {
                boards = boardRepository.findByMiniHomepageIdAndTypeAndIsPublicTrueAndCreatedAtBetween(miniHomepage.getId(), "DIARY", startOfDay, endOfDay);
            }
            boardPage = new PageImpl<>(boards, pageable, boards.size());
        } else if ("ILCHONPYEONG".equals(type)) {
            List<Board> allIlchonpyeongs = boardRepository.findAllByMiniHomepageIdAndTypeOrderByCreatedAtDesc(miniHomepage.getId(), type);
            boardPage = new PageImpl<>(allIlchonpyeongs, Pageable.unpaged(), allIlchonpyeongs.size());

        } else if ("GUESTBOOK".equals(type)) {
            boardPage = boardRepository.findByMiniHomepageIdAndType(miniHomepage.getId(), type, pageable);

        } else {
            if (isOwner) {
                boardPage = boardRepository.findByMiniHomepageIdAndType(miniHomepage.getId(), type, pageable);
            } else {
                boardPage = boardRepository.findByMiniHomepageIdAndTypeAndIsPublicTrue(miniHomepage.getId(), type, pageable);
            }
        }

        long totalElements = boardPage.getTotalElements();
        List<Board> boardList = boardPage.getContent();
        List<GetBoardResponseDto> dtoList = new ArrayList<>();

        Set<User> friends = boardList.stream()
                .filter(b -> "ILCHONPYEONG".equals(b.getType()))
                .map(Board::getUser)
                .collect(Collectors.toSet());

        Map<Integer, String> nicknameMap = ilchonRepository.findByUserAndFriendInAndStatusAndIsActiveTrue(
                miniHomepage.getUser(),
                friends,
                Ilchon.IlchonStatus.ACCEPTED)
                .stream()
                .collect(Collectors.toMap(
                        ilchon -> ilchon.getFriend().getId(),
                        Ilchon::getFriendNickname
                ));

        for (int i = 0; i < boardList.size(); i++) {
            Board board = boardList.get(i);
            String nickname = null;

            if ("ILCHONPYEONG".equals(board.getType())) {
                nickname = nicknameMap.get(board.getUser().getId());
            }

            Long boardNo = null;
            if (!"ILCHONPYEONG".equals(type)) {
                boardNo = totalElements - ((long) pageable.getPageNumber() * pageable.getPageSize()) - i;
            }

            if ("GUESTBOOK".equals(board.getType()) && !board.isPublic()) {
                boolean isWriter = (viewerId != null) && viewerId.equals(board.getUser().getId());

                if (!isOwner && !isWriter) {
                    dtoList.add(new GetBoardResponseDto(board, board.getUser(), boardNo, nickname, "🔒 비밀글입니다."));
                    continue;
                }
            }

            dtoList.add(new GetBoardResponseDto(board, board.getUser(), boardNo, nickname));
        }

        return new PageImpl<>(dtoList, pageable, totalElements);
    }

    @Override
    public List<GetBoardResponseDto> getRecentBoards(Integer userId, User viewer) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        List<String> allowedTypes = Arrays.asList("PHOTO", "DIARY", "JUKEBOX");
        boolean isOwner = (viewer != null) && viewer.getId().equals(userId);
        List<Board> recentBoards;

        if (isOwner) {
            recentBoards = boardRepository.findTop4ByMiniHomepageIdAndTypeInOrderByCreatedAtDesc(miniHomepage.getId(), allowedTypes);
        } else {
            recentBoards = boardRepository.findTop4ByMiniHomepageIdAndTypeInAndIsPublicTrueOrderByCreatedAtDesc(miniHomepage.getId(), allowedTypes);
        }

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

        boolean isPublicSetting = true;
        if ("ILCHONPYEONG".equals(type)) {
            isPublicSetting = true;
        }
        else if(requestDto.getPublicSetting() != null) {
            isPublicSetting = requestDto.getPublicSetting();
        }

        Board.BoardBuilder boardBuilder = Board.builder()
                .user(writer)
                .miniHomepage(targetHomepage)
                .type(type)
                .isPublic(isPublicSetting);

        if (requestDto.getContent() == null) {
            requestDto.setContent("");
        }

        switch (type) {
            case "PHOTO":
                if (requestDto.getImageUrls() == null || requestDto.getImageUrls().isEmpty()) {
                    throw new ValidationExceptionHandler("이미지를 첨부해주세요.");
                }
                if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty()) {
                    throw new ValidationExceptionHandler("제목을 입력해주세요.");
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

        if ("PHOTO".equals(requestDto.getType()) || "DIARY".equals(requestDto.getType())) {
            if (requestDto.getImageUrls() != null) {
                List<BoardImage> imageEntities = requestDto.getImageUrls().stream()
                        .map(url -> BoardImage.builder()
                                .board(newBoard)
                                .imageUrl(url)
                                .build())
                        .collect(Collectors.toList());

                newBoard.addImages(imageEntities);
            }
        }

        boardRepository.save(newBoard);
    }


    @Override
    @Transactional
    public void updateBoard(Integer boardId, Integer currentUserId, UpdateBoardRequestDto requestDto) {
        Board board = entityFinder.getBoardOrThrow(boardId);
        User currentUser = entityFinder.getUserOrThrow(currentUserId);
        boolean isWriter = board.getUser().getId().equals(currentUserId);

        if (!isWriter) {
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }

        board.update(requestDto);
    }


    @Override
    @Transactional
    public void updateBoardPrivacy(Integer boardId, Integer currentUserId, UpdateBoardPrivacyDto requestDto) {
        Board board = entityFinder.getBoardOrThrow(boardId);
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        boolean isWriter = board.getUser().getId().equals(currentUserId);
        boolean isOwner = board.getMiniHomepage().getUser().getId().equals(currentUserId);

        if ("GUESTBOOK".equals(board.getType())) {
            if (!isWriter && !isOwner) {
                throw new AccessDeniedException("게시글 공개 설정을 변경할 권한이 없습니다.");
            }
        } else {
            if (!isWriter) {
                throw new AccessDeniedException("게시글 공개 설정을 변경할 권한이 없습니다.");
            }
        }

        board.updatePrivacy(requestDto.getPublicSetting());
    }



    @Override
    @Transactional
    public void deleteBoard(Integer boardId, Integer currentUserId) {
        Board board = entityFinder.getBoardOrThrow(boardId);
        User currentUser = entityFinder.getUserOrThrow(currentUserId);

        boolean isOwner = board.getMiniHomepage().getUser().getId().equals(currentUser.getId());

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
    @Transactional(readOnly = true)
    public Map<String, BoardCountDto> getBoardCounts(Integer userId) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        List<String> types = Arrays.asList("PHOTO", "DIARY", "JUKEBOX", "GUESTBOOK", "ILCHONPYEONG");
        LocalDateTime recentDate = LocalDateTime.now().minusDays((7));

        List<BoardRepository.BoardCountByTypeDto> countsDto = boardRepository.countBoardsByType(
                miniHomepage.getId(), types, recentDate
        );

        Map<String, BoardCountDto> countsMap = countsDto.stream()
                .collect(Collectors.toMap(
                        BoardRepository.BoardCountByTypeDto::getType,
                        dto -> new BoardCountDto(dto.getNewCount(), dto.getTotalCount())
                ));

        for (String type : types) {
            countsMap.putIfAbsent(type, new BoardCountDto(0L, 0L));
        }

        return countsMap;
    }


    public List<Board> getBoardHistory(Integer boardId) {
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Number> revisions = reader.getRevisions(Board.class, boardId);

        List<Board> history = new ArrayList<>();
        for (Number rev : revisions) {
            Board pastBoard = reader.find(Board.class, boardId, rev);
            history.add(pastBoard);
        }
        return history;
    }
}

