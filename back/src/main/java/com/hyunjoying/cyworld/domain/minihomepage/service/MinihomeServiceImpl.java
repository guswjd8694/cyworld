package com.hyunjoying.cyworld.domain.minihomepage.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.minihomepage.entity.Visit;
import com.hyunjoying.cyworld.domain.minihomepage.repository.MinihomeRepository;
import com.hyunjoying.cyworld.domain.minihomepage.repository.VisitsRepository;
import com.hyunjoying.cyworld.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MinihomeServiceImpl implements MinihomeService {
    private final MinihomeRepository minihomeRepository;
    private final VisitsRepository visitsRepository;
    private final EntityFinder entityFinder;

    @Override
    @Transactional(readOnly = true)
    public GetMinihomeResponseDto getMinihomeInfo(Integer userId) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        return new GetMinihomeResponseDto(miniHomepage);
    }

    @Override
    @Transactional
    public void updateMinihomeTitle(Integer userId, UpdateMinihomeRequestDto requestDto) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);
        miniHomepage.updateTitle(requestDto.getTitle());
    }

    @Override
    @Transactional
    public GetMinihomeResponseDto recordAndIncrementVisit(Integer ownerUserId, Integer visitorId) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdWithLockOrThrow(ownerUserId);
        User visitor = (visitorId != null) ? entityFinder.getUserOrThrow(visitorId) : null;

        miniHomepage.incrementTodayAndTotalVisits();

        Visit visit = new Visit(miniHomepage, visitor);

        if (visitor == null) {
            visit.setCreatedBy(0);
        }

        visitsRepository.save(visit);

        return new GetMinihomeResponseDto(miniHomepage);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetTodayVisitCounts() {
        minihomeRepository.resetAllTodayVisits();
    }
}

