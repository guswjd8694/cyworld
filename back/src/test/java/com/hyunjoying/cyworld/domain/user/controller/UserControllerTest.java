//package com.hyunjoying.cyworld.domain.user.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hyunjoying.cyworld.common.service.FileUploadService;
//import com.hyunjoying.cyworld.common.util.JwtUtil;
//import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
//import com.hyunjoying.cyworld.domain.ilchon.repository.IlchonRepository;
//import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
//import com.hyunjoying.cyworld.domain.minihomepage.repository.MinihomeRepository;
//import com.hyunjoying.cyworld.domain.user.dto.request.SignUpRequestDto;
//import com.hyunjoying.cyworld.domain.user.dto.request.UpdateUserRequestDto;
//import com.hyunjoying.cyworld.domain.user.entity.User;
//import com.hyunjoying.cyworld.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@DisplayName("UserController 통합 테스트")
//class UserControllerTest {
//
//    @TestConfiguration
//    static class UserControllerTestConfig {
//        @Bean
//        public FileUploadService fileUploadService() {
//            return Mockito.mock(FileUploadService.class);
//        }
//    }
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private MinihomeRepository minihomeRepository;
//    @Autowired
//    private IlchonRepository ilchonRepository;
//    @Autowired
//    private JwtUtil jwtUtil;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    private User testUser1;
//    private User testUser2;
//    private User testUser3_Ilchon;
//    private String user1Token;
//    private String user2Token;
//
//    @BeforeEach
//    void setUp() {
//        testUser1 = User.builder()
//                .loginId("testUser1").password(passwordEncoder.encode("1234")).name("테스트유저1")
//                .email("test1@test.com").phone("010-1111-1111").birth("2000-01-01")
//                .gender(User.Gender.MALE).build();
//        userRepository.save(testUser1);
//        minihomeRepository.save(new MiniHomepage(testUser1));
//
//        testUser2 = User.builder()
//                .loginId("testUser2").password(passwordEncoder.encode("1234")).name("테스트유저2")
//                .email("test2@test.com").phone("010-2222-2222").birth("2000-01-01")
//                .gender(User.Gender.FEMALE).build();
//        userRepository.save(testUser2);
//        minihomeRepository.save(new MiniHomepage(testUser2));
//
//        testUser3_Ilchon = User.builder()
//                .loginId("ilchonUser").password(passwordEncoder.encode("1234")).name("일촌유저")
//                .email("ilchon@test.com").phone("010-3333-3333").birth("2000-01-01")
//                .gender(User.Gender.FEMALE).build();
//        userRepository.save(testUser3_Ilchon);
//        minihomeRepository.save(new MiniHomepage(testUser3_Ilchon));
//
//        ilchonRepository.save(Ilchon.builder().user(testUser1).friend(testUser3_Ilchon).status(Ilchon.IlchonStatus.ACCEPTED).friendNickname("일촌").isActive(true).build());
//        ilchonRepository.save(Ilchon.builder().user(testUser3_Ilchon).friend(testUser1).status(Ilchon.IlchonStatus.ACCEPTED).friendNickname("테스트유저1").isActive(true).build());
//
//        user1Token = jwtUtil.createToken(testUser1.getLoginId(), testUser1.getId(), testUser1.getName());
//        user2Token = jwtUtil.createToken(testUser2.getLoginId(), testUser2.getId(), testUser2.getName());
//    }
//
//
//    @Nested
//    @DisplayName("회원가입")
//    class SignUpTest {
//
//        @Test
//        @DisplayName("성공")
//        void signUp_Success() throws Exception {
//            // given
//            SignUpRequestDto requestDto = SignUpRequestDto.builder()
//                    .loginId("newUser").password("newPass123!").name("신규유저")
//                    .email("new@test.com").phone("010-1234-5678").birth("2000-10-10")
//                    .gender("MALE").build();
//
//            // when
//            ResultActions actions = mockMvc.perform(post("/users")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(requestDto)));
//
//            // then
//            actions.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다."))
//                    .andDo(print());
//
//            User newUser = userRepository.findByLoginIdAndIsDeletedFalse("newUser")
//                    .orElseThrow(() -> new AssertionError("회원가입된 사용자를 DB에서 찾을 수 없습니다."));
//
//            assertThat(newUser).isNotNull();
//            assertThat(newUser.getName()).isEqualTo("신규유저");
//            assertThat(passwordEncoder.matches("newPass123!", newUser.getPassword())).isTrue();
//
//            MiniHomepage newHomepage = minihomeRepository.findByUserIdAndIsDeletedFalse(newUser.getId())
//                    .orElseThrow(() -> new AssertionError("생성된 미니홈피를 DB에서 찾을 수 없습니다."));
//
//            assertThat(newHomepage).isNotNull();
//            assertThat(newHomepage.getUser().getId()).isEqualTo(newUser.getId());
//        }
//
//        @Test
//        @DisplayName("실패 - 중복된 로그인 ID")
//        void signUp_DuplicateLoginId_ThrowsConflict() throws Exception {
//            // given
//            SignUpRequestDto requestDto = SignUpRequestDto.builder()
//                    .loginId("testUser1")
//                    .password("newPass123!").name("신규유저").email("new@test.com")
//                    .phone("010-1234-5678").birth("2000-10-10").gender("MALE").build();
//
//            // when
//            ResultActions actions = mockMvc.perform(post("/users")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(requestDto)));
//
//            // then
//            actions.andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.message").value("이미 사용 중인 로그인 ID입니다."))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("실패 - 유효성 검사 실패 (ex: 이메일 형식)")
//        void signUp_InvalidEmail_ThrowsBadRequest() throws Exception {
//            // given
//            SignUpRequestDto requestDto = SignUpRequestDto.builder()
//                    .loginId("newUser").password("newPass123!").name("신규유저")
//                    .email("not-an-email")
//                    .phone("010-1234-5678").birth("2000-10-10").gender("MALE").build();
//
//            // when
//            ResultActions actions = mockMvc.perform(post("/users")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(requestDto)));
//
//            // then
//            actions.andExpect(status().isBadRequest())
//                    .andDo(print());
//        }
//    }
//
//
//    @Nested
//    @DisplayName("개인정보 수정")
//    class UpdateUserTest {
//
//        @Test
//        @DisplayName("성공 - 본인이 본인 정보 수정")
//        void updateUser_Success() throws Exception {
//            // given
//            UpdateUserRequestDto requestDto = new UpdateUserRequestDto("new-email@test.com", "010-5555-5555");
//
//            // when
//            ResultActions actions = mockMvc.perform(put("/users/{userId}", testUser1.getId())
//                    .header("Authorization", "Bearer " + user1Token)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(requestDto)));
//
//            // then
//            actions.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.message").value("개인정보가 성공적으로 수정되었습니다."))
//                    .andDo(print());
//
//            User updatedUser = userRepository.findById(testUser1.getId()).orElseThrow();
//            assertThat(updatedUser.getEmail()).isEqualTo("new-email@test.com");
//            assertThat(updatedUser.getPhone()).isEqualTo("010-5555-5555");
//        }
//
//        @Test
//        @DisplayName("실패 - 타인이 본인 정보 수정")
//        void updateUser_AsOther_ThrowsForbidden() throws Exception {
//            // given
//            UpdateUserRequestDto requestDto = new UpdateUserRequestDto("hacked@test.com", "010-9999-9999");
//
//            // when
//            ResultActions actions = mockMvc.perform(put("/users/{userId}", testUser1.getId())
//                    .header("Authorization", "Bearer " + user2Token)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(requestDto)));
//
//            // then
//            actions.andExpect(status().isForbidden())
//                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("실패 - 인증 없이 정보 수정")
//        void updateUser_Unauthorized_ThrowsForbidden() throws Exception {
//            // given
//            UpdateUserRequestDto requestDto = new UpdateUserRequestDto("hacked@test.com", "010-9999-9999");
//
//            // when
//            ResultActions actions = mockMvc.perform(put("/users/{userId}", testUser1.getId())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(requestDto)));
//
//            // then
//            actions.andExpect(status().isForbidden())
//                    .andDo(print());
//        }
//    }
//
//
//    @Nested
//    @DisplayName("회원 탈퇴")
//    class WithdrawUserTest {
//
//        @Test
//        @DisplayName("성공 - 본인이 본인 계정 탈퇴")
//        void withdrawUser_Success() throws Exception {
//            // when
//            ResultActions actions = mockMvc.perform(delete("/users/{userId}", testUser1.getId())
//                    .header("Authorization", "Bearer " + user1Token));
//
//            // then
//            actions.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.message").value("회원 탈퇴가 성공적으로 처리되었습니다."))
//                    .andDo(print());
//
//            User withdrawnUser = userRepository.findById(testUser1.getId()).orElseThrow();
//            assertThat(withdrawnUser.isDeleted()).isTrue();
//            assertThat(withdrawnUser.getDeletedAt()).isNotNull();
//        }
//
//        @Test
//        @DisplayName("실패 - 타인이 본인 계정 탈퇴")
//        void withdrawUser_AsOther_ThrowsForbidden() throws Exception {
//            // when
//            ResultActions actions = mockMvc.perform(delete("/users/{userId}", testUser1.getId())
//                    .header("Authorization", "Bearer " + user2Token));
//
//            // then
//            actions.andExpect(status().isForbidden())
//                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
//                    .andDo(print());
//        }
//    }
//
//
//    @Nested
//    @DisplayName("사용자 정보 조회")
//    class GetUserTest {
//
//        @Test
//        @DisplayName("성공 - 로그인 ID로 조회")
//        void getUserByLoginId_Success() throws Exception {
//            // when
//            ResultActions actions = mockMvc.perform(get("/users")
//                    .param("loginId", testUser1.getLoginId()));
//
//            // then
//            actions.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.userId").value(testUser1.getId()))
//                    .andExpect(jsonPath("$.loginId").value("testUser1"))
//                    .andExpect(jsonPath("$.name").value("테스트유저1"))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("실패 - 없는 로그인 ID로 조회")
//        void getUserByLoginId_NotFound() throws Exception {
//            // when
//            ResultActions actions = mockMvc.perform(get("/users")
//                    .param("loginId", "nonExistingUser"));
//
//            // then
//            actions.andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("성공 - ID로 조회")
//        void getUserById_Success() throws Exception {
//            // when
//            ResultActions actions = mockMvc.perform(get("/users/{userId}", testUser1.getId()));
//
//            // then
//            actions.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.userId").value(testUser1.getId()))
//                    .andExpect(jsonPath("$.loginId").value("testUser1"))
//                    .andExpect(jsonPath("$.name").value("테스트유저1"))
//                    .andDo(print());
//        }
//    }
//
//
//    @Nested
//    @DisplayName("랜덤 사용자 조회")
//    class GetRandomUserTest {
//
//        @Test
//        @DisplayName("성공 - 파도타기 (본인 제외)")
//        void getRandomUserForVisit_Success() throws Exception {
//
//            // when
//            ResultActions actions = mockMvc.perform(get("/users/random-visit")
//                    .header("Authorization", "Bearer " + user1Token));
//
//            // then
//            actions.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.userId", notNullValue()))
//                    .andExpect(jsonPath("$.loginId").value(
//                            org.hamcrest.Matchers.anyOf(
//                                    org.hamcrest.Matchers.is("testUser2"),
//                                    org.hamcrest.Matchers.is("ilchonUser")
//                            )
//                    ))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("성공 - 친구 추천 (본인, 일촌 제외)")
//        void getRandomUserForRecommendation_Success() throws Exception {
//
//            // when
//            ResultActions actions = mockMvc.perform(get("/users/random-recommendation")
//                    .header("Authorization", "Bearer " + user1Token));
//
//            // then
//            actions.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.userId").value(testUser2.getId()))
//                    .andExpect(jsonPath("$.loginId").value("testUser2"))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("실패(204) - 추천할 사용자가 없음")
//        void getRandomUserForRecommendation_NoContent() throws Exception {
//            userRepository.delete(testUser2);
//
//            // when
//            ResultActions actions = mockMvc.perform(get("/users/random-recommendation")
//                    .header("Authorization", "Bearer " + user1Token));
//
//            // then
//            actions.andExpect(status().isNoContent())
//                    .andDo(print());
//        }
//    }
//}