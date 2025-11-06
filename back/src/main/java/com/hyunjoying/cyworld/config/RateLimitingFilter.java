package com.hyunjoying.cyworld.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 20;
    private static final long TIME_FRAME_IN_MILLIS = 60 * 1000;

    private final Map<String, Queue<Long>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = getIp(request);
        long currentTime = System.currentTimeMillis();

        Queue<Long> requests = requestCounts.computeIfAbsent(ip, k -> new ConcurrentLinkedQueue<>());

        while (!requests.isEmpty() && (currentTime - requests.peek()) > TIME_FRAME_IN_MILLIS) {
            requests.poll();
        }

        if (requests.size() >= MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
            return;
        }

        requests.add(currentTime);
        filterChain.doFilter(request, response);
    }


    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }


    public void clear() {
        requestCounts.clear();
    }
}