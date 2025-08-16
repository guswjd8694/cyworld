//package com.hyunjoying.cyworld.user.service;
//
//import com.hyunjoying.cyworld.common.util.EntityFinder;
//import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
//import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;
//import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
//import com.hyunjoying.cyworld.domain.user.entity.User;
//import com.hyunjoying.cyworld.domain.minihomepage.service.MinihomeServiceImpl;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class MinihomeServiceImplTest {
//
//    @Mock
//    private EntityFinder entityFinder;
//
//    @InjectMocks
//    private MinihomeServiceImpl minihomeService;
//
//    @Test
//    @DisplayName("미니홈피 조회 성공")
//    void getMinihome_success() {
//        // given
//        Integer userId = 1;
//        User fakeUser = new User();
//        fakeUser.setId(userId);
//        fakeUser.setLoginId("hyunjoying");
//
//        MiniHomepage fakeMiniHomePage = new MiniHomepage();
//        fakeMiniHomePage.setUser(fakeUser);
//        fakeMiniHomePage.setTitle("현정이의 싸이월드");
//        fakeMiniHomePage.setTodayVisits(48);
//        fakeMiniHomePage.setTotalVisits(63518);
//
//        when(entityFinder.getMiniHomepageByUserIdOrThrow(userId)).thenReturn(fakeMiniHomePage);
//
//        // when
//        GetMinihomeResponseDto resultDto = minihomeService.getMinihome(userId);
//
//        // then
//        assertThat(resultDto).isNotNull();
//        assertThat(resultDto.getTitle()).isEqualTo("현정이의 싸이월드");
//        assertThat(resultDto.getTodayVisit()).isEqualTo(48);
//        assertThat(resultDto.getTotalVisit()).isEqualTo(63518);
//
//        verify(entityFinder, times(1)).getMiniHomepageByUserIdOrThrow(userId);
//    }
//
//    @Test
//    @DisplayName("미니홈피 조회 실패 - 존재하지 않는 유저")
//    void getMinihome_no_user() {
//        // given
//        Integer nonExistentUserId = 999;
//        when(entityFinder.getMiniHomepageByUserIdOrThrow(nonExistentUserId))
//                .thenThrow(new IllegalArgumentException("해당 사용자의 미니홈피가 존재하지 않습니다."));
//
//        // when & then
//        assertThrows(IllegalArgumentException.class, () -> {
//            minihomeService.getMinihome(nonExistentUserId);
//        });
//    }
//
//    @Test
//    @DisplayName("미니홈피 제목 수정 성공")
//    void updateMinihome_success() {
//        // given
//        Integer userId = 1;
//        String originalTitle = "현정이의 미니홈피";
//        String newTitle = "새로운 타이틀입니다";
//
//        User fakeUser = new User();
//        fakeUser.setId(userId);
//
//        MiniHomepage fakeMiniHomePage = new MiniHomepage();
//        fakeMiniHomePage.setUser(fakeUser);
//        fakeMiniHomePage.setTitle(originalTitle);
//
//        UpdateMinihomeRequestDto requestDto = new UpdateMinihomeRequestDto(newTitle);
//
//        when(entityFinder.getMiniHomepageByUserIdOrThrow(userId)).thenReturn(fakeMiniHomePage);
//
//        assertThat(fakeMiniHomePage.getTitle()).isEqualTo(originalTitle);
//
//        // when
//        minihomeService.updateMinihome(userId, requestDto);
//
//        // then
//        assertThat(fakeMiniHomePage.getTitle()).isEqualTo(newTitle);
//        verify(entityFinder, times(1)).getMiniHomepageByUserIdOrThrow(userId);
//    }
//
//    @Test
//    @DisplayName("매일 자정 투데이 카운트 초기화")
//    void resetTodayVisitCounts() {
//        // given
//        MinihomeRepository localMinihomeRepository = mock(MinihomeRepository.class);
//        MinihomeServiceImpl localMinihomeService = new MinihomeServiceImpl(localMinihomeRepository, null);
//
//        // when
//        localMinihomeService.resetTodayVisitCounts();
//
//        // then
//        verify(localMinihomeRepository, times(1)).resetAllTodayVisits();
//    }
//}
