package com.hyunjoying.cyworld.domain.board.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.domain.comment.entity.Comment;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.access.AccessDeniedException;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@SQLDelete(sql = "UPDATE board SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Filter(name = "deletedFilter")
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mini_homepage_id", nullable = false)
    private MiniHomepage miniHomepage;

    @Column(length = 30)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> images = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Column(nullable = false, length = 30)
    private String type;

    private String weather;
    private String mood;

    @Column(nullable = false)
    private boolean isPublic = true;

    @Builder
    public Board(User user, MiniHomepage miniHomepage, String title, String content, String type, String weather, String mood, Boolean isPublic) {
        this.user = user;
        this.miniHomepage = miniHomepage;
        this.title = title;
        this.content = content;
        this.type = type;
        this.weather = weather;
        this.mood = mood;
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
    }

    public void update(UpdateBoardRequestDto requestDto) {

        if (requestDto.getTitle() != null) {
            this.title = requestDto.getTitle();
        }

        if (requestDto.getContent() != null) {
            this.content = requestDto.getContent();
        }

        if (requestDto.getWeather() != null) {
            this.weather = requestDto.getWeather();
        }

        if (requestDto.getMood() != null) {
            this.mood = requestDto.getMood();
        }

        if (requestDto.getPublicSetting() != null) {
            this.isPublic = requestDto.getPublicSetting();
        }

        if (requestDto.getImageUrls() != null) {
            List<String> newImageUrls = requestDto.getImageUrls();

            this.images.forEach(existingImage -> {
                if (!newImageUrls.contains(existingImage.getImageUrl())) {
                    existingImage.deactivate();
                }
            });

            newImageUrls.forEach(newUrl -> {
                boolean isAlreadyExist = this.images.stream()
                        .anyMatch(img -> img.getImageUrl().equals(newUrl));
                if (!isAlreadyExist) {
                    this.addImages(List.of(BoardImage.builder().board(this).imageUrl(newUrl).build()));
                }
            });
        }
    }

    public List<BoardImage> getActiveImages() {
        return this.images.stream()
                .filter(BoardImage::getIsActive)
                .collect(Collectors.toList());
    }


    public void checkDeletionPermission(User currentUser) {
        Integer authorId = this.user.getId();
        Integer ownerId = this.miniHomepage.getUser().getId();
        Integer currentUserId = currentUser.getId();

        if (!currentUserId.equals(authorId) && !currentUserId.equals(ownerId)) {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }
    }


    public void addImages(List<BoardImage> imageEntities) {
        this.images.addAll(imageEntities);
    }

    public void clearImages() {
        this.images.forEach(BoardImage::disassociateBoard);
        this.images.clear();
    }


    public void updatePrivacy(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}

