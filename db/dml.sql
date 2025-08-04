-- 데이터 삽입 순서에 제약이 없도록 외래 키 체크를 임시로 비활성화합니다.
SET FOREIGN_KEY_CHECKS = 0;

-- 1. emotions 테이블 (마스터 데이터)
-- 시스템 관리자(ID: 0)가 생성했다고 가정합니다.
INSERT INTO `emotions` (`id`, `type`, `name`, `created_by`) VALUES
    (1, '긍정', '🌷 행복', 0),
    (2, '일상', '🤔 생각중', 0),
    (3, '부정', '💧 슬픔', 0),
    (4, '긍정', '🥰 설렘', 0),
    (5, '일상', '😴 졸림', 0);

-- 2. users 테이블 (사용자 8명)
-- created_by는 자기 자신의 ID로 설정합니다.
INSERT INTO `users` (`id`, `emotion_id`, `name`, `email`, `login_id`, `password`, `birth`, `gender`, `created_by`) VALUES
    (1, 1, '김현정', 'khj@email.com', 'user1', 'hashed_pw_1', '1999-01-28', 'Female', 1),
    (2, 2, '홍우진', 'hwj@email.com', 'user2', 'hashed_pw_2', '1998-05-15', 'Male', 2),
    (3, 4, '장유나', 'jyn@email.com', 'user3', 'hashed_pw_3', '2000-11-02', 'Female', 3),
    (4, 1, '홍은지', 'hej@email.com', 'user4', 'hashed_pw_4', '1999-07-22', 'Female', 4),
    (5, 5, '김진영', 'kjy@email.com', 'user5', 'hashed_pw_5', '1997-03-10', 'Male', 5),
    (6, 2, '정혜윤', 'jhy@email.com', 'user6', 'hashed_pw_6', '2001-09-30', 'Female', 6),
    (7, 3, '장은석', 'jes@email.com', 'user7', 'hashed_pw_7', '1996-12-12', 'Male', 7),
    (8, 1, '남궁성', 'ngs@email.com', 'user8', 'hashed_pw_8', '1995-06-01', 'Male', 8);

-- 3. mini_homepages 테이블 (사용자별 미니홈피)
INSERT INTO `mini_homepages` (`id`, `user_id`, `today_visits`, `total_visits`, `title`, `created_by`) VALUES
    (1, 1, 25, 12345, '현정이의 미니홈피', 1),
    (2, 2, 10, 8765, '홍우진 월드', 2),
    (3, 3, 33, 24680, '유나의 다이어리', 3),
    (4, 4, 15, 9982, '은지의 사진첩', 4),
    (5, 5, 5, 5050, '김진영 홈피', 5),
    (6, 6, 40, 31234, '혜윤이네', 6),
    (7, 7, 8, 7654, '장은석 미니홈피', 7),
    (8, 8, 22, 15432, '남궁성의 공간', 8);

-- 4. user_profiles 테이블 (프로필 이력)
INSERT INTO `user_profiles` (`id`, `user_id`, `image_url`, `bio`, `created_by`, `updated_by`, `is_active`) VALUES
    (1, 1, '/images/profile/user1_old.jpg', '옛날 자기소개~', 1, 1, FALSE),
    (2, 1, '/images/profile/user1_new.jpg', 'Sㅏi좋은 Sㅏ람들의 Sㅏ이버 Sㅔ상', 1, 1, TRUE),
    (3, 2, '/images/profile/user2.jpg', '반갑습니다!', 2, 2, TRUE),
    (4, 3, '/images/profile/user3.jpg', '일촌 환영해요 *^^*', 3, 3, TRUE);

-- 5. board 테이블 (게시판: 다이어리, 방명록, 사진첩)
INSERT INTO `board` (`id`, `user_id`, `mini_homepage_id`, `content`, `type`, `created_by`, `updated_by`) VALUES
    (1, 1, 1, '오늘 팀 프로젝트 너무 재밌었다! 싸이월드 클론코딩 파이팅!', 'DIARY', 1, 1),
    (2, 2, 1, '현정님 홈피 잘 보고 갑니다~ 일촌해요!', 'GUESTBOOK', 2, 2),
    (3, 3, 1, '안녕하세요! 유나입니다. 방명록 남겨요~', 'GUESTBOOK', 3, 3),
    (4, 4, 2, '우진아~ 은지 왔다감!', 'GUESTBOOK', 4, 4),
    (5, 5, 3, '제주도 여행 사진입니다.', 'PHOTO', 5, 5);

-- 6. comments 테이블 (댓글)
INSERT INTO `comments` (`id`, `user_id`, `board_id`, `content`, `created_by`, `updated_by`) VALUES
    (1, 2, 1, '오~ 저도 재밌었어요! 내일도 파이팅!', 2, 2),
    (2, 1, 2, '네! 반갑습니다. 신청해주세요~', 1, 1),
    (3, 1, 3, '유나님 반가워요!! 자주 놀러오세요 :)', 1, 1);

-- 7. ilchons 테이블 (일촌 관계)
-- 김현정(1) <-> 홍우진(2) : 서로 일촌 수락 상태
INSERT INTO `ilchons` (`id`, `user_id`, `friend_id`, `user_nickname`, `friend_nickname`, `status`, `created_by`, `updated_by`) VALUES
    (1, 1, 2, '코딩천재우진', '귀요미현정', 'ACCEPTED', 1, 1);
-- 장유나(3) -> 홍은지(4) : 일촌 신청 상태
INSERT INTO `ilchons` (`id`, `user_id`, `friend_id`, `user_nickname`, `friend_nickname`, `status`, `created_by`, `updated_by`) VALUES
    (2, 3, 4, NULL, NULL, 'PENDING', 3, 3);

-- 8. visits 테이블 (방문 기록)
INSERT INTO `visits` (`id`, `mini_homepage_id`, `visitor_id`, `created_by`, `updated_by`) VALUES
    (1, 1, 2, 2, 2),
    (2, 1, 3, 3, 3),
    (3, 2, 1, 1, 1),
    (4, 1, 5, 5, 5);

-- 9. bgm 테이블 (배경음악)
INSERT INTO `bgm` (`id`, `mini_homepage_id`, `bgm_url`, `title`, `artist`, `created_by`, `updated_by`) VALUES
    (1, 1, '/music/freestyle_y.mp3', 'Y (Please Tell Me Why)', '프리스타일', 1, 1),
    (2, 2, '/music/epichigh_love.mp3', 'Love Love Love', '에픽하이', 2, 2),
    (3, 3, '/music/younha_pw.mp3', '비밀번호 486', '윤하', 3, 3);

-- 비활성화했던 외래 키 체크를 다시 활성화합니다.
SET FOREIGN_KEY_CHECKS = 1;
