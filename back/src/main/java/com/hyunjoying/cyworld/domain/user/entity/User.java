package com.hyunjoying.cyworld.domain.user.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.emotion.entity.Emotion;
import com.hyunjoying.cyworld.domain.user.converter.GenderConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id")
    private Emotion emotion;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(nullable = false, length = 50)
    private String birth;

    @Convert(converter = GenderConverter.class)
    @Column(nullable = false, columnDefinition = "ENUM('MALE', 'FEMALE')")
    private Gender gender;

    public enum Gender {
        MALE, FEMALE
    }

    public User(String loginId, String password, String name, String email, String phone, String birth, Gender gender) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birth = birth;
        this.gender = gender;
    }

    public void updateUserInfo(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateEmotion(Emotion emotion) {
        this.emotion = emotion;
    }


}
