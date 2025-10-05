package com.hyunjoying.cyworld.domain.bgm.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bgm")
@SQLDelete(sql = "UPDATE bgm SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Bgm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "bgm_url", nullable = false)
    private String bgmUrl;

    @Column(nullable = false)
    private String title;

    @Column(length = 50, nullable = false)
    private String artist;
}

