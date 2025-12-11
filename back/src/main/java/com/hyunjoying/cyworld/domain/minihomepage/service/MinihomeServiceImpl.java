package com.hyunjoying.cyworld.domain.minihomepage.service;

import com.hyunjoying.cyworld.common.util.EntityFinder;
import com.hyunjoying.cyworld.domain.minihomepage.dto.request.UpdateMinihomeRequestDto;
import com.hyunjoying.cyworld.domain.minihomepage.dto.response.GetMinihomeResponseDto;
import com.hyunjoying.cyworld.domain.minihomepage.entity.MiniHomepage;
import com.hyunjoying.cyworld.domain.minihomepage.entity.Visit;
import com.hyunjoying.cyworld.domain.minihomepage.repository.MinihomeRepository;
import com.hyunjoying.cyworld.domain.minihomepage.repository.VisitsRepository;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MinihomeServiceImpl implements MinihomeService {
    private final MinihomeRepository minihomeRepository;
    private final VisitsRepository visitsRepository;
    private final EntityFinder entityFinder;

    @Override
    @Transactional(readOnly = true)
    public GetMinihomeResponseDto getMinihomeInfoByLoginId(String loginId) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserLoginIdOrThrow(loginId);
        return new GetMinihomeResponseDto(miniHomepage);
    }

    @Override
    @Transactional
    public void updateMinihomeTitle(String loginId, UpdateMinihomeRequestDto requestDto) {
        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserLoginIdOrThrow(loginId);
        miniHomepage.updateTitle(requestDto.getTitle());
    }

    @Override
    @Transactional
    public GetMinihomeResponseDto recordAndIncrementVisit(String ownerLoginId, Integer visitorId, HttpServletRequest request) {

        MiniHomepage miniHomepage = entityFinder.getMiniHomepageByUserLoginIdOrThrow(ownerLoginId);
        User visitor = (visitorId != null) ? entityFinder.getUserOrThrow(visitorId) : null;
        String visitorIp = getClientIp(request);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime tomorrowStartOfDay = today.plusDays(1).atStartOfDay();

        boolean isFirstVisitToday = !visitsRepository.existsByIpAndDateRange(
                miniHomepage,
                visitorIp,
                startOfDay,
                tomorrowStartOfDay
        );

        Visit visit = Visit.builder()
                .miniHomepage(miniHomepage)
                .visitor(visitor)
                .visitorIp(visitorIp)
                .build();
        if (visitor == null) {
            visit.setCreatedBy(0);
        }
        visitsRepository.save(visit);

        if (isFirstVisitToday) {
            miniHomepage.incrementTodayVisits();
            miniHomepage.incrementTotalVisits();
        }

        return new GetMinihomeResponseDto(miniHomepage);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetTodayVisitCounts() {
        minihomeRepository.resetAllTodayVisits();
    }



    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}

