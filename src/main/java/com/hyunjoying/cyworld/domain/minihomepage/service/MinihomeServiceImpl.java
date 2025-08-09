package com.hyunjoying.cyworld.domain.minihomepage.service;


import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.user.entity.User;
import com.hyunjoying.cyworld.domain.minihomepage.entity.Visit;
import com.hyunjoying.cyworld.domain.minihomepage.repository.MinihomeRepository;
import com.hyunjoying.cyworld.domain.minihomepage.repository.VisitsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MinihomeServiceImpl implements MinihomeService {
    private final VisitsRepository visitsRepository;
    private final MinihomeRepository minihomeRepository;
    private final EntityFinder entityFinder;


    @Override
    @Transactional
    public void recordVisitAndIncrementCounters(Integer userId, User visitor) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);

        miniHomepage.setTodayVisits(miniHomepage.getTodayVisits() + 1);
        miniHomepage.setTotalVisits(miniHomepage.getTotalVisits() + 1);

        Visit newVisit = new Visit();
        newVisit.setMiniHomepage(miniHomepage);

        if (visitor != null) {
            newVisit.setVisitor(visitor);
            newVisit.setCreatedBy(visitor.getId());
        } else {
            newVisit.setCreatedBy(0);
        }

        visitsRepository.save(newVisit);
    }


    @Override
    @Transactional(readOnly = true)
    public GetMinihomeResponseDto getMinihome(Integer userId) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);

        return new GetMinihomeResponseDto(
                miniHomepage.getTodayVisits(),
                miniHomepage.getTotalVisits(),
                miniHomepage.getTitle(),
                "cyworld.com/" + miniHomepage.getUser().getLoginId()
        );
    }


    @Override
    @Transactional
    public void updateMinihome(Integer userId, UpdateMinihomeRequestDto requestDto) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserIdOrThrow(userId);

        miniHomepage.setTitle(requestDto.getTitle());
    }


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetTodayVisitCounts() {
        minihomeRepository.resetAllTodayVisits();
    }
}