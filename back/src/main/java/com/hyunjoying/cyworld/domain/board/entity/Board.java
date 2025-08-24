package com.hyunjoying.cyworld.domain.board.entity;

import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE board SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
@Table(name = "board")
@Getter
@Setter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mini_homepage_id", nullable = false)
    private MiniHomepage miniHomepage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 30)
    private String type;

    private String weather;

    private String mood;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Integer updatedBy;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isPublic = true;


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
        this.setPublic(isPublic);
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
}
