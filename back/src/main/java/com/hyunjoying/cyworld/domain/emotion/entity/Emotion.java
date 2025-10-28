package com.hyunjoying.cyworld.domain.emotion.entity;

import com.hyunjoying.cyworld.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE emotions SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
@Table(name = "emotions")
@Getter
@Setter
@Audited
public class Emotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 50)
    private String name;
}