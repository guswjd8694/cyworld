package com.hyunjoying.cyworld.domain.minihomepage.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE bgm SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
@Table(name = "bgm")
@Getter
@Setter
public class Bgm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)  // n + 1 JPA 문제 찾아보기 -> 왜 Fetch 사용했는지
    @JoinColumn(name = "mini_homepage_id", nullable = false)
    private MiniHomepage miniHomepage;

    @Column(name = "bgm_url", nullable = false)
    private String bgmUrl;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 50)
    private String artist;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
