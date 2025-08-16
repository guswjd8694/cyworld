-- =================================================================
-- emotions 테이블 (감정 마스터 데이터)
-- =================================================================
CREATE TABLE `emotions` (
    `id`           INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `type`         VARCHAR(50)    NOT NULL   COMMENT '감정 타입 (예: 긍정, 부정, 일상)',
    `name`         VARCHAR(50)    NOT NULL   COMMENT '감정명 (예: 🌷 행복)',
    `created_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`   INT            NOT NULL   COMMENT '생성자',
    `updated_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`   INT            NULL       COMMENT '수정자',
    `deleted_at`   DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`   BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    PRIMARY KEY (`id`)
);

-- =================================================================
-- users 테이블 (사용자 정보)
-- =================================================================
CREATE TABLE `users` (
    `id`           INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `emotion_id`   INT            NULL       COMMENT 'emotions 식별자 (FK)',
    `name`         VARCHAR(50)    NOT NULL   COMMENT '이름',
    `email`        VARCHAR(255)   NOT NULL   COMMENT '이메일',
    `login_id`     VARCHAR(255)   NOT NULL   COMMENT '로그인 아이디',
    `password`     VARCHAR(255)   NOT NULL   COMMENT '비밀번호',
    `birth`        VARCHAR(50)    NOT NULL   COMMENT '생년월일',
    `gender`       ENUM('Male', 'Female') NOT NULL COMMENT '성별',
    `created_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`   INT            NOT NULL   COMMENT '생성자',
    `updated_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`   INT            NULL       COMMENT '수정자',
    `deleted_at`   DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`   BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`emotion_id`) REFERENCES `emotions`(`id`)
);

-- =================================================================
-- mini_homepages 테이블 (미니홈피 정보)
-- =================================================================
CREATE TABLE `mini_homepages` (
    `id`           INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `user_id`      INT            NOT NULL   COMMENT 'users 식별자',
    `today_visits` INT            NOT NULL   DEFAULT 0 COMMENT '하루 방문자 수',
    `total_visits` INT            NOT NULL   DEFAULT 0 COMMENT '전체 방문자 수',
    `title`        VARCHAR(100)   NOT NULL   COMMENT '미니홈피 제목',
    `created_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`   INT            NOT NULL   COMMENT '생성자',
    `updated_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`   INT            NULL       COMMENT '수정자',
    `deleted_at`   DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`   BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

-- =================================================================
-- user_profiles 테이블 (프로필 이력)
-- =================================================================
CREATE TABLE `user_profiles` (
    `id`           INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `user_id`      INT            NOT NULL   COMMENT 'users 식별자',
    `image_url`    VARCHAR(255)   NOT NULL   COMMENT '프로필 사진',
    `bio`          TEXT           NOT NULL   COMMENT '프로필 소개',
    `created_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`   INT            NOT NULL   COMMENT '생성자',
    `updated_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`   INT            NULL       COMMENT '수정자',
    `deleted_at`   DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`   BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    `is_active`    BOOLEAN        NOT NULL   DEFAULT TRUE COMMENT '활성 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

-- =================================================================
-- board 테이블 (게시판: 방명록, 다이어리, 사진첩 글)
-- =================================================================
CREATE TABLE `board` (
    `id`               INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `user_id`          INT            NOT NULL   COMMENT '작성자 users 식별자',
    `mini_homepage_id` INT            NOT NULL   COMMENT '게시된 mini_homepages 식별자',
    `content`          TEXT           NOT NULL   COMMENT '게시글 내용',
    `type`             VARCHAR(30)    NOT NULL   COMMENT '게시판 타입 (GUESTBOOK, DIARY, PHOTO)',
    `created_at`       DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`       INT            NOT NULL   COMMENT '생성자',
    `updated_at`       DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`       INT            NULL       COMMENT '수정자',
    `deleted_at`       DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`       BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`mini_homepage_id`) REFERENCES `mini_homepages`(`id`)
);

-- =================================================================
-- comments 테이블 (댓글)
-- =================================================================
CREATE TABLE `comments` (
    `id`           INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `user_id`      INT            NOT NULL   COMMENT '작성자 users 식별자',
    `board_id`     INT            NOT NULL   COMMENT 'board 식별자',
    `content`      VARCHAR(1000)  NOT NULL   COMMENT '댓글 내용',
    `created_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`   INT            NOT NULL   COMMENT '생성자',
    `updated_at`   DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`   INT            NULL       COMMENT '수정자',
    `deleted_at`   DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`   BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`board_id`) REFERENCES `board`(`id`)
);

-- =================================================================
-- ilchons 테이블 (일촌 관계)
-- =================================================================
CREATE TABLE `ilchons` (
    `id`               INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `user_id`          INT            NOT NULL   COMMENT '관계를 신청한 users 식별자',
    `friend_id`        INT            NOT NULL   COMMENT '관계를 신청받은 users 식별자',
    `user_nickname`    VARCHAR(100)   NULL       COMMENT 'user_id가 friend_id에게 붙인 별명',
    `friend_nickname`  VARCHAR(100)   NULL       COMMENT 'friend_id가 user_id에게 붙인 별명',
    `status`           VARCHAR(50)    NOT NULL   COMMENT '수락 상태 (PENDING, ACCEPTED)',
    `created_at`       DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`       INT            NOT NULL   COMMENT '생성자',
    `updated_at`       DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`       INT            NULL       COMMENT '수정자',
    `deleted_at`       DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`       BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`friend_id`) REFERENCES `users`(`id`)
);

-- =================================================================
-- visits 테이블 (방문 기록)
-- =================================================================
CREATE TABLE `visits` (
    `id`               INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `mini_homepage_id` INT            NOT NULL   COMMENT '방문한 mini_homepages 식별자',
    `visitor_id`       INT            NULL       COMMENT '방문자 users 식별자 (비회원일 경우 NULL)',
    `created_at`       DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`       INT            NOT NULL   COMMENT '생성자',
    `updated_at`       DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`       INT            NULL       COMMENT '수정자',
    `deleted_at`       DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`       BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`mini_homepage_id`) REFERENCES `mini_homepages`(`id`),
    FOREIGN KEY (`visitor_id`) REFERENCES `users`(`id`)
);

-- =================================================================
-- bgm 테이블 (배경음악)
-- =================================================================
CREATE TABLE `bgm` (
    `id`               INT            NOT NULL   AUTO_INCREMENT COMMENT 'PK',
    `mini_homepage_id` INT            NOT NULL   COMMENT 'mini_homepages 식별자',
    `bgm_url`          VARCHAR(255)   NOT NULL   COMMENT '노래 파일의 링크',
    `title`            VARCHAR(255)   NOT NULL   COMMENT '제목',
    `artist`           VARCHAR(50)    NOT NULL   COMMENT '가수',
    `created_at`       DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    `created_by`       INT            NOT NULL   COMMENT '생성자',
    `updated_at`       DATETIME(6)    NOT NULL   DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',
    `updated_by`       INT            NULL       COMMENT '수정자',
    `deleted_at`       DATETIME(6)    NULL       COMMENT '삭제 시간',
    `is_deleted`       BOOLEAN        NOT NULL   DEFAULT FALSE COMMENT '삭제 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`mini_homepage_id`) REFERENCES `mini_homepages`(`id`)
);
