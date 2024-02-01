package org.project.nuwabackend.global.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.dto.GlobalErrorResponseDto;
import org.project.nuwabackend.global.exception.DuplicationException;
import org.project.nuwabackend.global.exception.JwtException;
import org.project.nuwabackend.global.exception.LoginException;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.service.GlobalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdviceController {

    private final GlobalService globalService;

    // Duplicate Exception
    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<Object> duplicateException(DuplicationException dci) {
        log.warn("Duplicate Exception = {}", dci.getMessage());
        GlobalErrorResponseDto duplicateExceptionResponse =
                globalService.errorResponse(dci.getMessage());

        return ResponseEntity.status(CONFLICT).body(duplicateExceptionResponse);
    }

    // JwtException
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> jwtException(JwtException jep) {
        log.warn("Jwt Exception = {}", jep.getMessage());
        GlobalErrorResponseDto jwtExceptionResponse =
                globalService.errorResponse(jep.getMessage());

        return ResponseEntity.status(UNAUTHORIZED).body(jwtExceptionResponse);
    }

    // NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFoundException(NotFoundException nfe) {
        log.warn("NotFound Exception = {}", nfe.getMessage());
        GlobalErrorResponseDto notFoundExceptionResponse =
                globalService.errorResponse(nfe.getMessage());

        return ResponseEntity.status(NOT_FOUND).body(notFoundExceptionResponse);
    }

    // LoginException
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> loginException(LoginException lep) {
        log.warn("Login Exception = {}", lep.getMessage());
        GlobalErrorResponseDto loginExceptionResponse =
                globalService.errorResponse(lep.getMessage());

        return ResponseEntity.status(BAD_REQUEST).body(loginExceptionResponse);
    }

    // UsernameNotFoundException
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> usernameNotFoundException(UsernameNotFoundException unf) {
        log.warn("Username NotFound Exception = {}", unf.getMessage());
        GlobalErrorResponseDto loginExceptionResponse =
                globalService.errorResponse(unf.getMessage());

        return ResponseEntity.status(NOT_FOUND).body(loginExceptionResponse);
    }

    // IlleagalAccessError
    @ExceptionHandler(IllegalAccessError.class)
    public ResponseEntity<Object> IllegalAccessError(IllegalAccessError iae) {
        log.warn("Illegal Access Error = {}", iae.getMessage());
        GlobalErrorResponseDto illegalAccessErrorResponse =
                globalService.errorResponse(iae.getMessage());

        return ResponseEntity.status(FORBIDDEN).body(illegalAccessErrorResponse);
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
