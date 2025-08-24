package com.hyunjoying.cyworld.domain.minihomepage.entity;

import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE mini_homepages SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
@Table(name = "mini_homepages")
@Getter
@Setter
public class MiniHomepage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer todayVisits = 0;

    @Column(nullable = false)
    private Integer totalVisits = 0;

    @Column(nullable = false, length = 100)
    private String title;

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
}