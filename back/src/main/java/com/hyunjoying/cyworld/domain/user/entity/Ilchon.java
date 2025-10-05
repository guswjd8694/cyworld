package com.hyunjoying.cyworld.domain.user.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.user.converter.IlchonStatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ilchons", uniqueConstraints = {
        // @UniqueConstraint(columnNames = {"user_id", "friend_id", "status", "isActive"})
})
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
    private String userNickname;

    @Column(length = 100)
    private String friendNickname;

    @Convert(converter = IlchonStatusConverter.class)
    @Column(nullable = false, length = 50, columnDefinition = "ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELED', 'BROKEN')")
    private IlchonStatus status;

    @Lob
    private String requestMessage;

    @Column(nullable = false)
    private boolean isActive = true;

    public enum IlchonStatus {
        PENDING,    // 신청 중
        ACCEPTED,   // 수락됨 (일촌 관계)
        REJECTED,   // 거절됨
        CANCELED,   // 신청자가 취소
        BROKEN      // 관계 끊김
    }
}

