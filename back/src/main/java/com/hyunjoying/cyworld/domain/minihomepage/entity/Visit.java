package com.hyunjoying.cyworld.domain.minihomepage.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE visits SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
@Table(name = "visits")
public class Visit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mini_homepage_id", nullable = false)
    private MiniHomepage miniHomepage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id")
    private User visitor;

    public Visit(MiniHomepage miniHomepage, User visitor) {
        this.miniHomepage = miniHomepage;
        this.visitor = visitor;
    }
}
