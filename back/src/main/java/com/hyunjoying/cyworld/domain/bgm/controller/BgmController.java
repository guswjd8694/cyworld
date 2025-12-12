package com.hyunjoying.cyworld.domain.bgm.controller;

import com.hyunjoying.cyworld.domain.bgm.dto.response.GetPlaylistResponseDto;
import com.hyunjoying.cyworld.domain.bgm.service.BgmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BgmController {

    private final BgmService bgmService;

    @GetMapping("/users/{userId}/bgms")
    public ResponseEntity<List<GetPlaylistResponseDto>> getBgms(@PathVariable Integer userId) {
        List<GetPlaylistResponseDto> bgms = bgmService.getBgmByUserId(userId);
        return ResponseEntity.ok(bgms);
    }
//dd
}
