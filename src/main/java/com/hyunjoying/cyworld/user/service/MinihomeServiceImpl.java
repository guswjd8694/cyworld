package com.hyunjoying.cyworld.user.service;


import com.hyunjoying.cyworld.user.dto.request.UpdateMinihomeRequestDto;
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


    @Override
    @Transactional
    public void recordVisitAndIncrementCounters(Integer miniHomepageId, User visitor) {
        MiniHomepage miniHomepage = getMinihomepageById(miniHomepageId);

        miniHomepage.setTodayVisits(miniHomepage.getTodayVisits() + 1);
        miniHomepage.setTotalVisits(miniHomepage.getTotalVisits() + 1);

        Visit newVisit = new Visit();
        newVisit.setMiniHomepage(miniHomepage);
        newVisit.setVisitor(visitor);

        visitsRepository.save(newVisit);
    }

    @Override
    @Transactional
    public void updateMinihomeTitle(Integer miniHomepageId, UpdateMinihomeRequestDto requestDto) {
        MiniHomepage miniHomepage = getMinihomepageById(miniHomepageId);
        miniHomepage.setTitle(requestDto.getTitle());
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetTodayVisitCounts() {
        minihomeRepository.resetAllTodayVisits();
    }


    private MiniHomepage getMinihomepageById(Integer miniHomepageId) {
        return minihomeRepository.findById(miniHomepageId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 미니홈피를 찾을 수 없습니다: " + miniHomepageId));
    }


    @Override
    public Integer getTodayVisit(Integer miniHomepageId) {
        return getMinihomepageById(miniHomepageId).getTodayVisits();
    }

    @Override
    public Integer getTotalVisit(Integer miniHomepageId) {
        return getMinihomepageById(miniHomepageId).getTotalVisits();
    }

    @Override
    public String getTitle(Integer miniHomepageId) {
        return getMinihomepageById(miniHomepageId).getTitle();
    }

    @Override
    public String getUrl(Integer miniHomepageId) {
        String loginId = getMinihomepageById(miniHomepageId).getUser().getLoginId();
        return  "cyworld.com/" + loginId;
    }
}