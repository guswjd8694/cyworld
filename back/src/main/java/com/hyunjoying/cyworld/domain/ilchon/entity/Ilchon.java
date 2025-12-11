package com.hyunjoying.cyworld.domain.ilchon.entity;

import com.hyunjoying.cyworld.domain.ilchon.converter.IlchonStatusConverter;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ilchons")
@Audited
public class Ilchon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IlchonStatus status;

    public enum IlchonStatus {
        ACCEPTED,   // 수락됨 (일촌 관계)
        BROKEN      // 관계 끊김
    }
}

