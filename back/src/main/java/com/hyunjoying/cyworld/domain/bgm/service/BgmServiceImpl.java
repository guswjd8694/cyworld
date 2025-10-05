package com.hyunjoying.cyworld.domain.bgm.service;

import com.hyunjoying.cyworld.domain.bgm.dto.response.GetPlaylistResponseDto;
import com.hyunjoying.cyworld.domain.bgm.entity.Bgm;
import com.hyunjoying.cyworld.domain.bgm.repository.BgmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BgmServiceImpl implements BgmService {

    private final BgmRepository bgmRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GetPlaylistResponseDto> getBgmByUserId(Integer userId) {
        List<Bgm> bgmList = bgmRepository.findAllByUserIdAndIsDeletedFalse(userId);

        return bgmList.stream()
                .map(GetPlaylistResponseDto::new)
                .collect(Collectors.toList());
    }
}
