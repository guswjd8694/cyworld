package com.hyunjoying.cyworld.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String message;
}
