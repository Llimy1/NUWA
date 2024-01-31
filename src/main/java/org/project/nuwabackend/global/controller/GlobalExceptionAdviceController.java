package org.project.nuwabackend.global.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.dto.GlobalErrorResponseDto;
import org.project.nuwabackend.global.exception.Duplication;
import org.project.nuwabackend.global.service.GlobalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdviceController {

    private final GlobalService globalService;

    // Duplicate Exception
    @ExceptionHandler(Duplication.class)
    public ResponseEntity<Object> duplicateException(Duplication dci) {
        log.warn("Duplicate Exception = {}", dci.getMessage());
        GlobalErrorResponseDto duplicateExceptionResponse =
                globalService.errorResponse(dci.getMessage());

        return ResponseEntity.status(CONFLICT).body(duplicateExceptionResponse);
    }

    // Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception e) {
        log.warn("Exception = {}", e.getMessage());
        GlobalErrorResponseDto exceptionResponse =
                globalService.errorResponse(e.getMessage());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

}
