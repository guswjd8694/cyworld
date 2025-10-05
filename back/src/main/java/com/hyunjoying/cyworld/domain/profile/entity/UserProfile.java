package com.hyunjoying.cyworld.domain.profile.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import com.hyunjoying.cyworld.domain.profile.dto.request.UpdateProfileRequestDto;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@SQLDelete(sql = "UPDATE user_profiles SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @Lob
    private String bio;

    @Column(nullable = false)
    private boolean isActive = true;

    public UserProfile(User user, String gender) {
        this.user = user;
        this.bio = "자기소개를 입력해주세요.";
        if (User.Gender.MALE.name().equalsIgnoreCase(gender)) {
            this.imageUrl = "/imgs/default_img_male.jpg";
        } else {
            this.imageUrl = "/imgs/default_img_female.jpg";
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    public static UserProfile createNewVersion(User user, UpdateProfileRequestDto dto, UserProfile oldProfile) {
        UserProfile newProfile = new UserProfile();
        newProfile.user = user;
        newProfile.bio = dto.getBio() != null ? dto.getBio() : oldProfile.getBio();
        newProfile.imageUrl = dto.getImageUrl() != null ? dto.getImageUrl() : oldProfile.getImageUrl();
        newProfile.isActive = true;
        return newProfile;
    }
}

