package com.hyunjoying.cyworld.user.service;

import com.hyunjoying.cyworld.user.dto.response.schma.ProfileHistoryDto;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class ProfileServiceImpl implements ProfileService {
    public String getName(Long userId) {
        return "김현정";
    }

    public String getGender(Long userId) {
        return "Female";
    }

    public Date getBirthday(Long userId) {
        return new Date();
    }

    public String getEmail(Long userId) {
        return "ajdrns8694@gmail.com";
    }

    public List<ProfileHistoryDto> getProfileHistory(Long userId) {
        return null;
    }
}
