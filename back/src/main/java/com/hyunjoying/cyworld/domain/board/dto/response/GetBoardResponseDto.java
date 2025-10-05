package com.hyunjoying.cyworld.domain.board.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.board.entity.BoardImage;
import com.hyunjoying.cyworld.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetBoardResponseDto {
    @Schema(example = "123", description = "게시글 고유 식별자(PK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer boardId;

    @Schema(example = "1", description = "작성자 고유 식별자(PK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer writerId;

    @Schema(example = "52", description = "게시판 내 순차 번호 (방명록, 사진첩 등)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Long boardNo;

    @Schema(example = "김현정", description = "작성자 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String writerName;

    @Schema(example = "오늘의 일기", description = "게시글 제목")
    private final String title;

    @Schema(example = "<p>오늘 하루도 즐거웠다!</p>", description = "게시글 내용 (HTML 형식)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String content;

    @Schema(description = "게시글에 첨부된 이미지 URL 목록")
    private final List<String> images;

    @Schema(example = "DIARY", description = "게시글 타입 (DIARY, PHOTO, GUESTBOOK)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String type;

    @Schema(example = "맑음", description = "다이어리 날씨")
    private final String weather;

    @Schema(example = "기쁨", description = "다이어리 기분")
    private final String mood;

    @Schema(example = "true", description = "공개 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("isPublic")
    private final boolean isPublic;

    @Schema(example = "2025-10-05T22:15:00", description = "생성 시간", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    @Schema(example = "2025-10-05T22:15:00", description = "최종 수정 시간", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Schema(example = "기욤현정짱짱", description = "작성자의 일촌명 (페이지 주인이 설정)")
    private final String writerNickname;

    @Schema(example = "user1", description = "작성자 로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String writerLoginId;

    @Schema(example = "오늘 하루도 즐거웠다!", description = "HTML 태그가 제거된 미리보기용 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String previewContent;


    public GetBoardResponseDto(Board board, User writer, Long boardNo, String writerNickname) {
        this.boardId = board.getId();
        this.writerId = writer.getId();
        this.boardNo = boardNo;
        this.writerName = writer.getName();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.images = board.getImages().stream()
                .map(BoardImage::getImageUrl)
                .collect(Collectors.toList());
        this.type = board.getType();
        this.weather = board.getWeather();
        this.mood = board.getMood();
        this.isPublic = board.isPublic();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
        this.writerNickname = writerNickname;
        this.writerLoginId = writer.getLoginId();
        this.previewContent = Jsoup.parse(board.getContent()).text();
    }
}