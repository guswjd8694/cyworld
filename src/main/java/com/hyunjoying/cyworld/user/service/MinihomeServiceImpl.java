package com.hyunjoying.cyworld.user.service;


import com.hyunjoying.cyworld.user.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.user.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.user.entity.MiniHomepage;
import com.hyunjoying.cyworld.user.entity.User;
import com.hyunjoying.cyworld.user.entity.Visit;
import com.hyunjoying.cyworld.user.repository.MinihomeRepository;
import com.hyunjoying.cyworld.user.repository.VisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MinihomeServiceImpl implements MinihomeService {

    @Autowired
    private VisitsRepository visitsRepository;
    @Autowired
    private MinihomeRepository minihomeRepository;


    private MiniHomepage getMinihomepageByUserId(Integer userId) {
        return minihomeRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 미니홈피를 찾을 수 없습니다: " + userId));
    }


    @Override
    @Transactional
    public void recordVisitAndIncrementCounters(Integer userId, User visitor) {
        MiniHomepage miniHomepage = getMinihomepageByUserId(userId);

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
        MiniHomepage miniHomepage = getMinihomepageByUserId(userId);

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
        MiniHomepage miniHomepage = getMinihomepageByUserId(userId);
        miniHomepage.setTitle(requestDto.getTitle());
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetTodayVisitCounts() {
        minihomeRepository.resetAllTodayVisits();
    }
}