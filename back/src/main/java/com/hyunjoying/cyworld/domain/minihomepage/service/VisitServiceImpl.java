package com.hyunjoying.cyworld.domain.minihomepage.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.minihomepage.entity.Visit;
import com.hyunjoying.cyworld.domain.minihomepage.repository.VisitsRepository;
import com.hyunjoying.cyworld.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {
    private final VisitsRepository visitsRepository;
    private final EntityFinder entityFinder;


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
}
