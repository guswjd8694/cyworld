package com.hyunjoying.cyworld.domain.board.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
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

import java.util.ArrayList;
import java.util.List;

@Entity
@SQLDelete(sql = "UPDATE board SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Filter(name = "deletedFilter")
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "original_board_id")
    private Integer originalBoardId;

    @Column(name = "version")
    private Integer version = 1;

    @Column(nullable = false)
    private boolean isPublic = true;

    @Builder
    public Board(User user, MiniHomepage miniHomepage, String title, String content, String type, String weather, String mood, Boolean isPublic, Integer originalBoardId, Integer version) {
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
        this.originalBoardId = originalBoardId;
        this.version = (version != null) ? version : 1;
    }


    public void updateContent(String newContent, User currentUser) {
        if (!this.user.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("게시글 내용을 수정할 권한이 없습니다.");
        }
        this.content = newContent;
        this.setUpdatedBy(currentUser.getId());
    }


    public void updatePrivacy(boolean isPublic, User currentUser) {
        if ("ILCHONPYEONG".equals(this.type)) {
            throw new IllegalArgumentException("일촌평은 공개/비공개 설정을 지원하지 않습니다.");
        }
        if (!this.user.getId().equals(currentUser.getId()) && !this.miniHomepage.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("공개/비공개 설정을 변경할 권한이 없습니다.");
        }
        this.isPublic = isPublic;
        this.setUpdatedBy(currentUser.getId());
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


    @PostPersist
    public void setOriginalIdOnFirstSave() {
        if (this.originalBoardId == null) {
            this.originalBoardId = this.id;
        }
    }
}

