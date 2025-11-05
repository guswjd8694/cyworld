package com.hyunjoying.cyworld.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunjoying.cyworld.common.util.JwtUtil;
import com.hyunjoying.cyworld.domain.board.dto.request.CreateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardPrivacyDto;
import com.hyunjoying.cyworld.domain.board.dto.request.UpdateBoardRequestDto;
import com.hyunjoying.cyworld.domain.board.entity.Board;
import com.hyunjoying.cyworld.domain.board.repository.BoardRepository;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.minihomepage.repository.MinihomeRepository;
import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRepository;
import com.hyunjoying.cyworld.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("BoardController 통합 테스트")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditorAware<Integer> auditorProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MinihomeRepository minihomeRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private IlchonRepository ilchonRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;      // 홈 주인
    private User testIlchon;    // 일촌
    private User testStranger;  // 방문자(회원)

    private String userToken;
    private String ilchonToken;
    private String strangerToken;

    private MiniHomepage testUserHomepage;

    @BeforeEach
    void setUp() {
        given(auditorProvider.getCurrentAuditor()).willReturn(Optional.of(1));

        // 홈 주인
        testUser = User.builder()
                .loginId("testUser").password("1234").name("테스트유저")
                .email("test@test.com").phone("010-1234-5678").birth("2000-01-01")
                .gender(User.Gender.MALE).build();
        userRepository.save(testUser);

        testUserHomepage = new MiniHomepage(testUser);
        minihomeRepository.save(testUserHomepage);

        // 일촌
        testIlchon = User.builder()
                .loginId("friend1").password("1234").name("테스트일촌")
                .email("friend@test.com").phone("010-5687-1234").birth("2000-01-01")
                .gender(User.Gender.FEMALE).build();
        userRepository.save(testIlchon);

        MiniHomepage ilchonHomepage = new MiniHomepage(testIlchon);
        minihomeRepository.save(ilchonHomepage);

        // 방문자(회원)
        testStranger = User.builder()
                .loginId("stranger").password("1234").name("방문자")
                .email("stranger@test.com").phone("010-9999-9999").birth("2000-01-01")
                .gender(User.Gender.FEMALE).build();
        userRepository.save(testStranger);

        Ilchon ilchon1 = Ilchon.builder().user(testUser).friend(testIlchon).status(Ilchon.IlchonStatus.ACCEPTED).friendNickname("일촌").isActive(true).build();
        ilchonRepository.save(ilchon1);

        Ilchon ilchon2 = Ilchon.builder().user(testIlchon).friend(testUser).status(Ilchon.IlchonStatus.ACCEPTED).friendNickname("테스트유저").isActive(true).build();
        ilchonRepository.save(ilchon2);

        userToken = jwtUtil.createToken(testUser.getLoginId(), testUser.getId(), testUser.getName());
        ilchonToken = jwtUtil.createToken(testIlchon.getLoginId(), testIlchon.getId(), testIlchon.getName());
        strangerToken = jwtUtil.createToken(testStranger.getLoginId(), testStranger.getId(), testStranger.getName());
    }


    @Test
    @DisplayName("날짜별 다이어리 조회 성공")
    void getBoards_byDate_Success() throws Exception {
        // given
        Board diary = Board.builder()
                .miniHomepage(testUserHomepage)
                .user(testUser)
                .type("DIARY")
                .content("에효")
                .build();

        boardRepository.saveAndFlush(diary);

        entityManager.createNativeQuery("UPDATE board SET created_at = ? WHERE id = ?")
                .setParameter(1, LocalDateTime.of(2025, 11, 1, 23, 30))
                .setParameter(2, diary.getId())
                .executeUpdate();

        entityManager.clear();

        // when & then
        mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                        .param("type", "DIARY")
                        .param("date", "2025-11-01")
                        .header("Authorization", "Bearer " + userToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].content").value("에효"))
                .andDo(print());
    }


    @Test
    @DisplayName("타입별 게시판 페이징 조회 성공")
    void getBoards_byTypeWithPaging_Success() throws Exception {
        for (int i = 0; i < 15; i++) {
            boardRepository.save(Board.builder()
                    .miniHomepage(testUserHomepage)
                    .user(testUser)
                    .type("PHOTO")
                    .content("사진 " + i)
                    .isPublic(true)
                    .build());
        }

        // when & then
        mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                        .param("type", "PHOTO")
                        .param("page", "1")
                        .param("size", "5")
                        .header("Authorization", "Bearer " + userToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(1))
                .andDo(print());
    }


    @Test
    @DisplayName("게시판 조회 실패 (필수 type 파라미터 누락)")
    void getBoards_missingTypeParam_ThrowsBadRequest() throws Exception {
        // when & then
        mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                        .param("date", "2025-11-01")
                        .header("Authorization", "Bearer " + userToken)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Nested
    @DisplayName("게시판 타입별 권한 조회")
    class GetBoardsByPrivacyTest {

        @Nested
        @DisplayName("사진첩 / 다이어리 타입")
        class PhotoAndDiaryPrivacyTest {

            @BeforeEach
            void setupPhotoAndDiaryData() {
                // given
                for (int i = 0; i < 2; i++) {
                    boardRepository.save(Board.builder()
                            .miniHomepage(testUserHomepage)
                            .user(testUser)
                            .type("PHOTO")
                            .content("전체 공개 사진 " + i)
                            .isPublic(true)
                            .build());
                }
                for (int i = 0; i < 3; i++) {
                    boardRepository.save(Board.builder()
                            .miniHomepage(testUserHomepage)
                            .user(testUser)
                            .type("PHOTO")
                            .content("비공개 사진 " + i)
                            .isPublic(false)
                            .build());
                }
            }

            @Test
            @DisplayName("본인이 조회 시 비공개 글 포함 5개 모두 조회")
            void asOwner_seesAll_5_Posts() throws Exception {
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "PHOTO")
                                .header("Authorization", "Bearer " + userToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(5))
                        .andExpect(jsonPath("$.totalElements").value(5))
                        .andDo(print());
            }

            @Test
            @DisplayName("일촌이 조회 시 전체 공개 글 2개만 조회")
            void asIlchon_seesOnlyPublic_2_Posts() throws Exception {
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "PHOTO")
                                .header("Authorization", "Bearer " + ilchonToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(2))
                        .andExpect(jsonPath("$.totalElements").value(2))
                        .andDo(print());
            }

            @Test
            @DisplayName("방문자가 조회 시 전체 공개 글 2개만 조회")
            void asStranger_seesOnlyPublic_2_Posts() throws Exception {
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "PHOTO")
                                .header("Authorization", "Bearer " + strangerToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(2))
                        .andExpect(jsonPath("$.totalElements").value(2))
                        .andDo(print());
            }

            @Test
            @DisplayName("비회원이 조회 시 전체 공개 글 2개만 조회")
            void asAnonymous_seesOnlyPublic_2_Posts() throws Exception {
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                        .param("type", "PHOTO")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(2))
                        .andExpect(jsonPath("$.totalElements").value(2))
                        .andDo(print());
            }
        }


        @Nested
        @DisplayName("방명록 타입")
        class GuestbookPrivacyTest {

            @BeforeEach
            void setupGuestbookData() {
                // given
                boardRepository.save(Board.builder()
                        .miniHomepage(testUserHomepage)
                        .user(testUser)
                        .type("GUESTBOOK")
                        .content("내가 쓴 공개 방명록")
                        .isPublic(true)
                        .build());
                boardRepository.save(Board.builder()
                        .miniHomepage(testUserHomepage)
                        .user(testUser)
                        .type("GUESTBOOK")
                        .content("내가 쓴 비밀 방명록")
                        .isPublic(false)
                        .build());

                boardRepository.save(Board.builder()
                        .miniHomepage(testUserHomepage)
                        .user(testIlchon)
                        .type("GUESTBOOK")
                        .content("일촌이 쓴 공개 방명록")
                        .isPublic(true)
                        .build());
                boardRepository.save(Board.builder()
                        .miniHomepage(testUserHomepage)
                        .user(testIlchon)
                        .type("GUESTBOOK")
                        .content("일촌이 쓴 비밀 방명록")
                        .isPublic(false)
                        .build());
                boardRepository.save(Board.builder()
                        .miniHomepage(testUserHomepage)
                        .user(testStranger)
                        .type("GUESTBOOK")
                        .content("방문자가 쓴 비밀 방명록")
                        .isPublic(false)
                        .build());
            }

            @Test
            @DisplayName("홈주인이 조회 시 모든 글 5개의 원본 내용 조회")
            void asOwner_seesAll_5_OriginalContents() throws Exception {
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "GUESTBOOK")
                                .header("Authorization", "Bearer " + userToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(5))
                        .andExpect(jsonPath("$.content[?(@.content == '🔒 비밀글입니다.')]").isEmpty())
                        .andExpect(jsonPath("$.content[?(@.content == '일촌이 쓴 비밀 방명록')]").exists())
                        .andExpect(jsonPath("$.content[?(@.content == '방문자가 쓴 비밀 방명록')]").exists())
                        .andDo(print());
            }

            @Test
            @DisplayName("일촌이면서 글쓴이일 경우 조회 시 5개 목록, 본인 글(비밀 포함)은 원본, 남의 비밀글은 마스킹")
            void asWriterIlchon_seesOwnOriginal_andMaskedOthers() throws Exception {
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "GUESTBOOK")
                                .header("Authorization", "Bearer " + ilchonToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(5))
                        .andExpect(jsonPath("$.content[?(@.content == '일촌이 쓴 비밀 방명록')]").exists())
                        .andExpect(jsonPath("$.content[?(@.content == '방문자가 쓴 비밀 방명록')]").doesNotExist())
                        .andExpect(jsonPath("$.content[?(@.content == '내가 쓴 비밀 방명록')]").doesNotExist())
                        .andExpect(jsonPath("$.content[?(@.content == '🔒 비밀글입니다.')]", hasSize(2)))
                        .andDo(print());
            }

            @Test
            @DisplayName("방문자이면서 글쓴이일 경우 조회 시 5개 목록, 공개글은 원본, 남의 비밀글(3개)은 마스킹")
            void asStranger_seesPublicOriginal_andMaskedSecrets() throws Exception {
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "GUESTBOOK")
                                .header("Authorization", "Bearer " + strangerToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(5))
                        .andExpect(jsonPath("$.content[?(@.content == '내가 쓴 비밀 방명록')]").doesNotExist())
                        .andExpect(jsonPath("$.content[?(@.content == '일촌이 쓴 비밀 방명록')]").doesNotExist())
                        .andExpect(jsonPath("$.content[?(@.content == '방문자가 쓴 비밀 방명록')]").exists())
                        .andExpect(jsonPath("$.content[?(@.content == '🔒 비밀글입니다.')]", hasSize(2)))
                        .andDo(print());
            }
        }


        @Nested
        @DisplayName("일촌평 타입")
        class IlchonpyeongPrivacyTest {

            @BeforeEach
            void setupIlchonpyeongData() {
                for (int i = 0; i < 5; i++) {
                    boardRepository.save(Board.builder()
                            .miniHomepage(testUserHomepage)
                            .user(testIlchon)
                            .type("ILCHONPYEONG")
                            .content("일촌평 " + i)
                            .build());
                }
            }

            @Test
            @DisplayName("본인이 조회 시 5개 모두 조회")
            void asOwner_seesAll_5_Posts() throws Exception {
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "ILCHONPYEONG")
                                .header("Authorization", "Bearer " + userToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(5))
                        .andExpect(jsonPath("$.totalElements").value(5))
                        .andDo(print());
            }

            @Test
            @DisplayName("타인(일촌/방문자/비회원)이 조회 시 5개 모두 조회")
            void asOthers_seesAll_5_Posts() throws Exception {
                // when & then (일촌)
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "ILCHONPYEONG")
                                .header("Authorization", "Bearer " + ilchonToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(5));

                // when & then (방문자)
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "ILCHONPYEONG")
                                .header("Authorization", "Bearer " + strangerToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(5));

                // when & then (비회원)
                mockMvc.perform(get("/users/{userId}/boards", testUser.getId())
                                .param("type", "ILCHONPYEONG")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.length()").value(5));
            }
        }


        @Nested
        @DisplayName("최근 게시물 조회")
        class GetRecentBoardsTest {

            @Test
            @DisplayName("본인이 조회 시 비공개 포함, 허용된 타입(DIARY, PHOTO, JUKEBOX)의 최신 4개 조회")
            void asOwner_seesAllRecentAllowedTypes_4_Posts() throws Exception {
                // given
                createRecentTestData();

                // when & then
                mockMvc.perform(get("/users/{userId}/boards/recent", testUser.getId())
                                .header("Authorization", "Bearer " + userToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(4)))
                        .andExpect(jsonPath("$[0].content").value("오늘의 주크박스 (공개)"))
                        .andExpect(jsonPath("$[1].content").value("2일 전 일기 (비공개)"))
                        .andExpect(jsonPath("$[2].content").value("3일 전 사진 (공개)"))
                        .andExpect(jsonPath("$[3].content").value("4일 전 사진 (비공개)"))
                        .andDo(print());
            }

            @Test
            @DisplayName("타인이 조회 시: 공개된, 허용된 타입의 최신 4개(중 3개) 조회")
            void asStranger_seesOnlyPublicRecentAllowedTypes_3_Posts() throws Exception {
                // given
                createRecentTestData();

                // when & then (방문자)
                mockMvc.perform(get("/users/{userId}/boards/recent", testUser.getId())
                                .header("Authorization", "Bearer " + strangerToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(3)))
                        .andExpect(jsonPath("$[0].content").value("오늘의 주크박스 (공개)"))
                        .andExpect(jsonPath("$[1].content").value("3일 전 사진 (공개)"))
                        .andExpect(jsonPath("$[2].content").value("5일 전 일기 (공개)"))
                        .andDo(print());

                // when & then (비회원)
                mockMvc.perform(get("/users/{userId}/boards/recent", testUser.getId())
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(3)))
                        .andExpect(jsonPath("$[0].content").value("오늘의 주크박스 (공개)"))
                        .andDo(print());
            }

            @Test
            @DisplayName("게시물이 없을 때: 빈 리스트 반환")
            void whenNoPosts_returnsEmptyList() throws Exception {
                // given

                // when & then
                mockMvc.perform(get("/users/{userId}/boards/recent", testUser.getId())
                                .header("Authorization", "Bearer " + userToken)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)))
                        .andDo(print());
            }


            private void createRecentTestData() {
                LocalDateTime now = LocalDateTime.now();

                // 방명록 1개 포함 총 6개의 글 등록

                Board diary_public_5d_ago = boardRepository.saveAndFlush(Board.builder()
                        .miniHomepage(testUserHomepage).user(testUser).type("DIARY")
                        .content("5일 전 일기 (공개)").isPublic(true).build());
                entityManager.createNativeQuery("UPDATE board SET created_at = ? WHERE id = ?")
                        .setParameter(1, now.minusDays(5)).setParameter(2, diary_public_5d_ago.getId()).executeUpdate();

                Board photo_private_4d_ago = boardRepository.saveAndFlush(Board.builder()
                        .miniHomepage(testUserHomepage).user(testUser).type("PHOTO")
                        .content("4일 전 사진 (비공개)").isPublic(false).build());
                entityManager.createNativeQuery("UPDATE board SET created_at = ? WHERE id = ?")
                        .setParameter(1, now.minusDays(4)).setParameter(2, photo_private_4d_ago.getId()).executeUpdate();

                Board photo_public_3d_ago = boardRepository.saveAndFlush(Board.builder()
                        .miniHomepage(testUserHomepage).user(testUser).type("PHOTO")
                        .content("3일 전 사진 (공개)").isPublic(true).build());
                entityManager.createNativeQuery("UPDATE board SET created_at = ? WHERE id = ?")
                        .setParameter(1, now.minusDays(3)).setParameter(2, photo_public_3d_ago.getId()).executeUpdate();

                Board diary_private_2d_ago = boardRepository.saveAndFlush(Board.builder()
                        .miniHomepage(testUserHomepage).user(testUser).type("DIARY")
                        .content("2일 전 일기 (비공개)").isPublic(false).build());
                entityManager.createNativeQuery("UPDATE board SET created_at = ? WHERE id = ?")
                        .setParameter(1, now.minusDays(2)).setParameter(2, diary_private_2d_ago.getId()).executeUpdate();

                Board guestbook_ignored_1d_ago = boardRepository.saveAndFlush(Board.builder()
                        .miniHomepage(testUserHomepage).user(testIlchon).type("GUESTBOOK")
                        .content("1일 전 방명록 (무시됨)").isPublic(true).build());
                entityManager.createNativeQuery("UPDATE board SET created_at = ? WHERE id = ?")
                        .setParameter(1, now.minusDays(1)).setParameter(2, guestbook_ignored_1d_ago.getId()).executeUpdate();

                Board jukebox_public_today = boardRepository.saveAndFlush(Board.builder()
                        .miniHomepage(testUserHomepage).user(testUser).type("JUKEBOX")
                        .content("오늘의 주크박스 (공개)").isPublic(true).build());
                entityManager.createNativeQuery("UPDATE board SET created_at = ? WHERE id = ?")
                        .setParameter(1, now).setParameter(2, jukebox_public_today.getId()).executeUpdate();

                entityManager.clear();
            }
        }
    }


    @Nested
    @DisplayName("게시글 생성")
    class CreateBoardTest {

        @BeforeEach
        void cleanUpBoardTables() {
            entityManager.createNativeQuery("DELETE FROM board_images").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM comments").executeUpdate();

            entityManager.createNativeQuery("DELETE FROM board").executeUpdate();

            entityManager.clear();
        }

        @Test
        @DisplayName("인증(토큰) 없이 게시글 생성 시 403 반환")
        void whenNotAuthenticated_throwsForbidden() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("DIARY")
                    .content("비로그인 테스트")
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }

        @Test
        @DisplayName("본인이 본인 홈에 [사진첩] 작성 성공")
        void asOwner_createPublicPhoto_Success() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("PHOTO")
                    .title("내 첫 사진")
                    .content("내용입니다")
                    .imageUrls(List.of("https://example.com/image.jpg"))
                    .publicSetting(true)
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 등록되었습니다."))
                    .andDo(print());

            List<Board> boards = boardRepository.findAll();
            assertThat(boards).hasSize(1);
            assertThat(boards.get(0).getType()).isEqualTo("PHOTO");
            assertThat(boards.get(0).getTitle()).isEqualTo("내 첫 사진");
            assertThat(boards.get(0).isPublic()).isTrue();
        }

        @Test
        @DisplayName("본인이 본인 홈에 [다이어리] 작성 성공")
        void asOwner_createPrivateDiary_Success() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("DIARY")
                    .content("오늘의 비밀 일기")
                    .weather("맑음")
                    .mood("행복")
                    .imageUrls(Collections.emptyList())
                    .publicSetting(false)
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 등록되었습니다."))
                    .andDo(print());

            List<Board> boards = boardRepository.findAll();
            assertThat(boards).hasSize(1);
            assertThat(boards.get(0).getType()).isEqualTo("DIARY");
            assertThat(boards.get(0).getWeather()).isEqualTo("맑음");
            assertThat(boards.get(0).isPublic()).isFalse();
        }

        @Test
        @DisplayName("일촌이 남의 홈에 [방명록] 작성 성공")
        void asIlchon_createPublicGuestbook_Success() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("GUESTBOOK")
                    .content("일촌 왔다감")
                    .publicSetting(true)
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                            .header("Authorization", "Bearer " + ilchonToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 등록되었습니다."))
                    .andDo(print());

            List<Board> boards = boardRepository.findAll();
            assertThat(boards).hasSize(1);
            assertThat(boards.get(0).getType()).isEqualTo("GUESTBOOK");
            assertThat(boards.get(0).getUser().getId()).isEqualTo(testIlchon.getId());
        }

        @Test
        @DisplayName("방문자가 남의 홈에 [방명록] 작성 성공")
        void asStranger_createPrivateGuestbook_Success() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("GUESTBOOK")
                    .content("방문자 왔다감 (비밀)")
                    .publicSetting(false)
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                            .header("Authorization", "Bearer " + strangerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 등록되었습니다."))
                    .andDo(print());

            List<Board> boards = boardRepository.findAll();
            assertThat(boards).hasSize(1);
            assertThat(boards.get(0).getType()).isEqualTo("GUESTBOOK");
            assertThat(boards.get(0).getUser().getId()).isEqualTo(testStranger.getId());
            assertThat(boards.get(0).isPublic()).isFalse();
        }

        @Test
        @DisplayName("일촌이 남의 홈에 [일촌평] 작성 성공")
        void asIlchon_createIlchonpyeong_Success() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("ILCHONPYEONG")
                    .content("너는... 짱...★")
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                            .header("Authorization", "Bearer " + ilchonToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 등록되었습니다."))
                    .andDo(print());

            List<Board> boards = boardRepository.findAll();
            assertThat(boards).hasSize(1);
            assertThat(boards.get(0).getType()).isEqualTo("ILCHONPYEONG");
            assertThat(boards.get(0).getUser().getId()).isEqualTo(testIlchon.getId());
        }

        @Test
        @DisplayName("방문자가 남의 홈에 [일촌평] 작성 실패")
        void asStranger_createIlchonpyeong_ThrowsAccessDenied() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("ILCHONPYEONG")
                    .content("일촌 아니지만 남겨봄")
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                            .header("Authorization", "Bearer " + strangerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }

        @Test
        @DisplayName("[사진첩] 작성 시 제목 누락하면 400 반환")
        void asOwner_createPhoto_MissingTitle_ThrowsBadRequest() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("PHOTO")
                    // .title(null)
                    .content("제목이 없어요")
                    .imageUrls(List.of("https://example.com/image.jpg"))
                    .publicSetting(true)
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("[사진첩] 작성 시 이미지 누락하면 400 반환")
        void asOwner_createPhoto_MissingImages_ThrowsBadRequest() throws Exception {
            // given
            CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
                    .type("PHOTO")
                    .title("이미지가 없어요")
                    .content("내용만 있어요")
                    // .imageUrls(null)
                    .publicSetting(true)
                    .build();

            // when & then
            mockMvc.perform(post("/users/{userId}/boards", testUser.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

    }


    @Nested
    @DisplayName("게시글 수정")
    class UpdateBoardTest {
        private Board boardByOwner;
        private Board boardByIlchon;
        private UpdateBoardRequestDto updateDto;

        @BeforeEach
        void setUp() {
            boardByOwner = boardRepository.save(Board.builder()
                    .miniHomepage(testUserHomepage).user(testUser).type("PHOTO")
                    .title("홈주인 원본 제목").content("홈주인 원본 내용").isPublic(true).build());

            boardByIlchon = boardRepository.save(Board.builder()
                    .miniHomepage(testUserHomepage).user(testIlchon).type("GUESTBOOK")
                    .content("일촌 원본 방명록").isPublic(true).build());

            Board boardByStranger = boardRepository.save(Board.builder()
                    .miniHomepage(testUserHomepage).user(testStranger).type("GUESTBOOK")
                    .content("방문자 원본 방명록").isPublic(false).build());

            updateDto = UpdateBoardRequestDto.builder()
                    .title("수정된 제목")
                    .content("수정된 내용입니다.")
                    .publicSetting(false)
                    .build();
        }

        @Test
        @DisplayName("글쓴이(홈주인)가 본인 게시글 수정 성공")
        void asOwner_updateOwnBoard_Success() throws Exception {
            // when & then
            mockMvc.perform(put("/boards/{boardId}", boardByOwner.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 수정되었습니다."))
                    .andDo(print());

            Board updatedBoard = boardRepository.findById(boardByOwner.getId()).orElseThrow();
            assertThat(updatedBoard.getTitle()).isEqualTo("수정된 제목");
            assertThat(updatedBoard.getContent()).isEqualTo("수정된 내용입니다.");
            assertThat(updatedBoard.isPublic()).isFalse();
        }

        @Test
        @DisplayName("글쓴이(일촌)가 본인 방명록 수정 성공")
        void asWriterIlchon_updateOwnGuestbook_Success() throws Exception {
            // when & then
            mockMvc.perform(put("/boards/{boardId}", boardByIlchon.getId())
                            .header("Authorization", "Bearer " + ilchonToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            Board updatedBoard = boardRepository.findById(boardByIlchon.getId()).orElseThrow();
            assertThat(updatedBoard.getContent()).isEqualTo("수정된 내용입니다.");
        }

        @Test
        @DisplayName("홈주인이 글쓴이가 아닌 게시글(일촌 방명록) 수정 실패")
        void asOwner_updateOthersGuestbook_ThrowsForbidden() throws Exception {
            // when & then
            mockMvc.perform(put("/boards/{boardId}", boardByIlchon.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("방문자가 남의 게시글 수정 실패")
        void asStranger_updateOthersBoard_ThrowsForbidden() throws Exception {
            // when & then
            mockMvc.perform(put("/boards/{boardId}", boardByOwner.getId())
                            .header("Authorization", "Bearer " + strangerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 게시글 수정 실패")
        void updateNonExistentBoard_ThrowsNotFound() throws Exception {
            // when & then
            mockMvc.perform(put("/boards/{boardId}", 99999)
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                    )
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }


    @Nested
    @DisplayName("게시글 공개 설정 변경")
    class UpdateBoardPrivacyTest {

        private Board photoByOwner;
        private Board guestbookByIlchon;
        private UpdateBoardPrivacyDto privacyDto;

        @BeforeEach
        void setUp() {
            photoByOwner = boardRepository.save(Board.builder()
                    .miniHomepage(testUserHomepage).user(testUser).type("PHOTO")
                    .content("공개 사진").isPublic(true).build());

            guestbookByIlchon = boardRepository.save(Board.builder()
                    .miniHomepage(testUserHomepage).user(testIlchon).type("GUESTBOOK")
                    .content("일촌이 쓴 비밀 방명록").isPublic(false).build());

            privacyDto = UpdateBoardPrivacyDto.builder()
                    .publicSetting(true)
                    .build();
        }

        @Test
        @DisplayName("글쓴이(홈주인)가 본인 사진첩 공개 설정 변경 성공")
        void asOwner_updateOwnPhotoPrivacy_Success() throws Exception {
            // given
            UpdateBoardPrivacyDto dto = UpdateBoardPrivacyDto.builder().publicSetting(false).build();

            // when & then
            mockMvc.perform(patch("/boards/{boardId}/privacy", photoByOwner.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("공개 설정이 변경되었습니다."))
                    .andDo(print());

            Board updatedBoard = boardRepository.findById(photoByOwner.getId()).orElseThrow();
            assertThat(updatedBoard.isPublic()).isFalse();
        }

        @Test
        @DisplayName("홈주인이 글쓴이가 아닌 방명록(GUESTBOOK) 공개 설정 변경 성공")
        void asOwner_updateOthersGuestbookPrivacy_Success() throws Exception {
            // when & then
            mockMvc.perform(patch("/boards/{boardId}/privacy", guestbookByIlchon.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(privacyDto))
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            Board updatedBoard = boardRepository.findById(guestbookByIlchon.getId()).orElseThrow();
            assertThat(updatedBoard.isPublic()).isTrue();
        }

        @Test
        @DisplayName("글쓴이(일촌)가 본인 방명록 공개 설정 변경 성공")
        void asWriterIlchon_updateOwnGuestbookPrivacy_Success() throws Exception {
            // when & then
            mockMvc.perform(patch("/boards/{boardId}/privacy", guestbookByIlchon.getId())
                            .header("Authorization", "Bearer " + ilchonToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(privacyDto))
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            Board updatedBoard = boardRepository.findById(guestbookByIlchon.getId()).orElseThrow();
            assertThat(updatedBoard.isPublic()).isTrue();
        }


        @Test
        @DisplayName("홈주인이 아닌 타인(일촌)이 남의 사진첩(PHOTO) 공개 설정 변경 실패")
        void asIlchon_updateOthersPhotoPrivacy_ThrowsForbidden() throws Exception {
            // when & then
            mockMvc.perform(patch("/boards/{boardId}/privacy", photoByOwner.getId())
                            .header("Authorization", "Bearer " + ilchonToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(privacyDto))
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                    .andDo(print());
        }
    }


    @Nested
    @DisplayName("게시글 삭제")
    class DeleteBoardTest {

        private Board boardByOwner;
        private Board boardByIlchon;

        @BeforeEach
        void setUp() {
            boardByOwner = boardRepository.save(Board.builder()
                    .miniHomepage(testUserHomepage).user(testUser).type("PHOTO")
                    .title("홈주인 글").content("삭제될 글 1").isPublic(true).build());

            boardByIlchon = boardRepository.save(Board.builder()
                    .miniHomepage(testUserHomepage).user(testIlchon).type("GUESTBOOK")
                    .content("일촌이 쓴 글").isPublic(true).build());
        }

        @Test
        @DisplayName("글쓴이(홈주인)가 본인 게시글 삭제 성공")
        void asOwner_deleteOwnBoard_Success() throws Exception {
            // when & then
            mockMvc.perform(delete("/boards/{boardId}", boardByOwner.getId())
                            .header("Authorization", "Bearer " + userToken)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 삭제되었습니다."))
                    .andDo(print());

            assertThat(boardRepository.findById(boardByOwner.getId())).isEmpty();
        }

        @Test
        @DisplayName("홈주인이 글쓴이가 아닌 게시글(일촌 방명록) 삭제 성공")
        void asOwner_deleteOthersBoard_Success() throws Exception {
            // when & then
            mockMvc.perform(delete("/boards/{boardId}", boardByIlchon.getId())
                            .header("Authorization", "Bearer " + userToken)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 삭제되었습니다."))
                    .andDo(print());

            assertThat(boardRepository.findById(boardByIlchon.getId())).isEmpty();
        }

        @Test
        @DisplayName("글쓴이(일촌)가 본인 게시글 삭제 성공")
        void asWriterIlchon_deleteOwnBoard_Success() throws Exception {
            // when & then
            mockMvc.perform(delete("/boards/{boardId}", boardByIlchon.getId())
                            .header("Authorization", "Bearer " + ilchonToken)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("게시글이 성공적으로 삭제되었습니다."))
                    .andDo(print());

            assertThat(boardRepository.findById(boardByIlchon.getId())).isEmpty();
        }


        @Test
        @DisplayName("방문자가 남의 게시글 삭제 실패")
        void asStranger_deleteOthersBoard_ThrowsForbidden() throws Exception {
            // when & then
            mockMvc.perform(delete("/boards/{boardId}", boardByOwner.getId())
                            .header("Authorization", "Bearer " + strangerToken)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 게시글 삭제 실패")
        void deleteNonExistentBoard_ThrowsNotFound() throws Exception {
            // when & then
            mockMvc.perform(delete("/boards/{boardId}", 99999)
                            .header("Authorization", "Bearer " + userToken)
                    )
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }

    }


    @Nested
    @DisplayName("게시판별 게시물 수 조회")
    class GetBoardCountsTest {

        @Test
        @DisplayName("각 타입별 게시물이 있을 때 수 반환")
        void getBoardCounts_WhenPostsExist_ReturnsCorrectCounts() throws Exception {
            // given
            for (int i = 0; i < 2; i++) {
                boardRepository.save(Board.builder().miniHomepage(testUserHomepage).user(testUser).type("PHOTO").content("공개 사진 " + i).isPublic(true).build());
            }
            for (int i = 0; i < 3; i++) {
                boardRepository.save(Board.builder().miniHomepage(testUserHomepage).user(testUser).type("PHOTO").content("비공개 사진 " + i).isPublic(false).build());
            }

            boardRepository.save(Board.builder().miniHomepage(testUserHomepage).user(testUser).type("DIARY").content("일기1").isPublic(false).build());

            boardRepository.save(Board.builder().miniHomepage(testUserHomepage).user(testIlchon).type("GUESTBOOK").content("방명록1").isPublic(true).build());
            boardRepository.save(Board.builder().miniHomepage(testUserHomepage).user(testStranger).type("GUESTBOOK").content("방명록2").isPublic(false).build());


            // when & then
            mockMvc.perform(get("/users/{userId}/board-counts", testUser.getId())
                    .header("Authorization", "Bearer " + userToken)
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.PHOTO.totalCount").value(5))
                    .andExpect(jsonPath("$.DIARY.totalCount").value(1))
                    .andExpect(jsonPath("$.GUESTBOOK.totalCount").value(2))
                    .andExpect(jsonPath("$.ILCHONPYEONG.totalCount").value(0))
                    .andExpect(jsonPath("$.JUKEBOX.totalCount").value(0))
                    .andDo(print());
        }


        @Test
        @DisplayName("게시물이 없으면 0 반환")
        void getBoardCounts_WhenNoPosts_ReturnsZeroCounts() throws Exception {
            // given

            // when & then
            mockMvc.perform(get("/users/{userId}/board-counts", testUser.getId())
                    .header("Authorization", "Bearer " + userToken)
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.PHOTO.totalCount").value(0))
                    .andExpect(jsonPath("$.DIARY.totalCount").value(0))
                    .andExpect(jsonPath("$.GUESTBOOK.totalCount").value(0))
                    .andExpect(jsonPath("$.ILCHONPYEONG.totalCount").value(0))
                    .andExpect(jsonPath("$.JUKEBOX.totalCount").value(0))
                    .andDo(print());
        }
    }
}

