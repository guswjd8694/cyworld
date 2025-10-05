package com.hyunjoying.cyworld.domain.minihomepage.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE mini_homepages SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
@Table(name = "mini_homepages")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MiniHomepage extends BaseEntity {

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

    public MiniHomepage(User user) {
        this.user = user;
        this.title = user.getName() + "님의 미니홈피";
        this.todayVisits = 0;
        this.totalVisits = 0;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void incrementTodayAndTotalVisits() {
        this.todayVisits++;
        this.totalVisits++;
    }


    public void resetTodayVisits() {
        this.todayVisits = 0;
    }
}