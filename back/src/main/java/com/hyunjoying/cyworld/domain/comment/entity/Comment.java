package com.hyunjoying.cyworld.domain.comment.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLDelete(sql = "UPDATE comments SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "original_comment_id")
    private Integer originalCommentId;

    @Column(name = "version")
    private Integer version = 1;

    @Builder
    public Comment(User user, Board board, String content, Integer originalCommentId, Integer version) {
        this.user = user;
        this.board = board;
        this.content = content;
        this.originalCommentId = originalCommentId;
        this.version = (version != null) ? version : 1;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    @PostPersist
    public void setOriginalIdOnFirstSave() {
        if (this.originalCommentId == null) {
            this.originalCommentId = this.id;
        }
    }
}