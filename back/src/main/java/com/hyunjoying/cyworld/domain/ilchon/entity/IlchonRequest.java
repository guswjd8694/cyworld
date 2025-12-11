package com.hyunjoying.cyworld.domain.ilchon.entity;


import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
        import lombok.*;


@Entity
@Table(name = "ilchons_request")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IlchonRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;  // 신청 보낸 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;    // 신청 받은 사람

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IlchonRequestStatus status;

    @Lob
    private String requestMessage;

    @Column(length = 100)
    private String nicknameForToUser;

    @Column(length = 100)
    private String nicknameForFromUser;

    public enum IlchonRequestStatus {
        PENDING,    // 신청 중
        ACCEPTED,   // 수락됨
        REJECTED,   // 거절됨
        CANCELED,   // 신청자가 취소
    }

}
