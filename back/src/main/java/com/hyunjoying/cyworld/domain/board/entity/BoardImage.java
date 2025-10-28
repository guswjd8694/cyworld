package com.hyunjoying.cyworld.domain.board.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "board_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
public class BoardImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) default 1")
    private Boolean isActive = true;

    @Builder
    public BoardImage(Board board, String imageUrl, Integer sequence) {
        this.board = board;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    public void disassociateBoard() {
        this.board = null;
    }

    public void deactivate() {
        this.isActive = false;
    }
}