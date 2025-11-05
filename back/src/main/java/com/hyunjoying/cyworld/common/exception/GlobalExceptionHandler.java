package com.hyunjoying.cyworld.common.exception;

import com.hyunjoying.cyworld.common.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // [404] 존재하지 않는 리소스를 요청했을 때
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // [403] 권한이 없는 사용자가 접근했을 때
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("handleAccessDenied: {}", ex.getMessage());
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.FORBIDDEN.value(),
                "접근 권한이 없습니다."
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }


    // [400] 서비스 로직 내 유효성 검사가 실패 했을 때
    @ExceptionHandler(ValidationExceptionHandler.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptionHandler(ValidationExceptionHandler ex) {
        log.warn("handleValidationExceptionHandler: {}", ex.getMessage());
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // [400] @Valid 유효성 검증에 실패했을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // [400] 필수 @RequestParam 파라미터가 누락됐을 때
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        String errorMessage = String.format("필수 파라미터 '%s'(이)가 누락되었습니다.", ex.getParameterName());
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // [405] 허용되지 않은 HTTP 메서드로 요청했을 때
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // [500] 위에서 처리하지 못한 모든 예외를 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        System.err.println(ex.toString());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부에서 오류가 발생했습니다. 관리자에게 문의해주세요."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
