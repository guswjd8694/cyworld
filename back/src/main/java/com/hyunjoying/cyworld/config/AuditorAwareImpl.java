package com.hyunjoying.cyworld.config;

import com.hyunjoying.cyworld.domain.user.details.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty(); // 로그인하지 않은 경우
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return Optional.of(userDetails.getUser().getId());
    }
}