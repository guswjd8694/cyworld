package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.user.entity.MiniHomepage;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.repository.MinihomeRepository;
import com.hyunjoying.cyworld.user.repository.VisitsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinihomeServiceImplTest {
    @Mock
    private MinihomeRepository minihomeRepository;

    @InjectMocks
    private MinihomeServiceImpl minihomeService;

    @Test
    @DisplayName("미니홈피 조회 성공")
    void getMinihome_success() {
        // given
        Integer userId = 1;
        User fakeUser = new User();

        fakeUser.setId(userId);
        fakeUser.setLoginId("hyunjoying");

        MiniHomepage fakeMiniHomePage = new MiniHomepage();
        fakeMiniHomePage.setUser(fakeUser);
        fakeMiniHomePage.setTitle("현정이의 싸이월드");
        fakeMiniHomePage.setTodayVisits(48);
        fakeMiniHomePage.setTotalVisits(63518);

        when(minihomeRepository.findByUserId(userId)).thenReturn(Optional.of(fakeMiniHomePage));


        // when
        GetMinihomeResponseDto resultDto = minihomeService.getMinihome(userId);


        // then
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getTitle()).isEqualTo("현정이의 싸이월드");
        assertThat(resultDto.getTodayVisit()).isEqualTo(48);
        assertThat(resultDto.getTotalVisit()).isEqualTo(63518);
        assertThat(resultDto.getUrl()).isEqualTo("cyworld.com/hyunjoying");

        verify(minihomeRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("미니홈피 조회 실패 - 존재하지 않는 유저")
    void getMinihome_no_user() {
        // given
        Integer nonExistentUserId = 999;
        when(minihomeRepository.findByUserId(nonExistentUserId)).thenReturn(Optional.empty());


        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            minihomeService.getMinihome(nonExistentUserId);
        });
    }

    @Test
    @DisplayName("미니홈피 제목 수정 성공")
    void updateMinihome_success() {
        // given
        Integer userId = 1;
        String originalTitle = "현정이의 미니홈피";
        String newTitle = "새로운 타이틀입니다";

        User fakeUser = new User();
        fakeUser.setId(userId);

        MiniHomepage fakeMiniHomePage = new MiniHomepage();
        fakeMiniHomePage.setUser(fakeUser);
        fakeMiniHomePage.setTitle(originalTitle);

        UpdateMinihomeRequestDto requestDto = new UpdateMinihomeRequestDto(newTitle);

        when(minihomeRepository.findByUserId(userId)).thenReturn(Optional.of(fakeMiniHomePage));

        assertThat(fakeMiniHomePage.getTitle()).isEqualTo(originalTitle);


        // when
        minihomeService.updateMinihome(userId, requestDto);


        // then
        assertThat(fakeMiniHomePage.getTitle()).isEqualTo(newTitle);
        verify(minihomeRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("매일 자정 투데이 카운트 초기화")
    void resetTodayVisitCounts() {
        // when
        minihomeService.resetTodayVisitCounts();

        // then
        verify(minihomeRepository, times(1)).resetAllTodayVisits();
    }
}