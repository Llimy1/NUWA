package org.project.nuwabackend.global.exception.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.exception.custom.DuplicationException;
import org.project.nuwabackend.global.exception.custom.JwtException;
import org.project.nuwabackend.global.exception.custom.LoginException;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.global.exception.custom.OAuth2Exception;
import org.project.nuwabackend.global.response.dto.GlobalErrorResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
        log.error("Duplicate Exception = {}", dci.getMessage());
        GlobalErrorResponseDto duplicateExceptionResponse =
                globalService.errorResponse(dci.getMessage());

        return ResponseEntity.status(CONFLICT).body(duplicateExceptionResponse);
    }

    // JwtException
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> jwtException(JwtException jep) {
        log.error("Jwt Exception = {}", jep.getMessage());
        GlobalErrorResponseDto jwtExceptionResponse =
                globalService.errorResponse(jep.getMessage());

        return ResponseEntity.status(UNAUTHORIZED).body(jwtExceptionResponse);
    }

    // NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFoundException(NotFoundException nfe) {
        log.error("NotFound Exception = {}", nfe.getMessage());
        GlobalErrorResponseDto notFoundExceptionResponse =
                globalService.errorResponse(nfe.getMessage());

        return ResponseEntity.status(NOT_FOUND).body(notFoundExceptionResponse);
    }

    // LoginException
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> loginException(LoginException lep) {
        log.error("Login Exception = {}", lep.getMessage());
        GlobalErrorResponseDto loginExceptionResponse =
                globalService.errorResponse(lep.getMessage());

        return ResponseEntity.status(BAD_REQUEST).body(loginExceptionResponse);
    }

    // UsernameNotFoundException
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> usernameNotFoundException(UsernameNotFoundException unf) {
        log.error("Username NotFound Exception = {}", unf.getMessage());
        GlobalErrorResponseDto loginExceptionResponse =
                globalService.errorResponse(unf.getMessage());

        return ResponseEntity.status(NOT_FOUND).body(loginExceptionResponse);
    }

    // IlleagalAccessError
    @ExceptionHandler(IllegalAccessError.class)
    public ResponseEntity<Object> IllegalAccessError(IllegalAccessError iae) {
        log.error("Illegal Access Error = {}", iae.getMessage());
        GlobalErrorResponseDto illegalAccessErrorResponse =
                globalService.errorResponse(iae.getMessage());

        return ResponseEntity.status(FORBIDDEN).body(illegalAccessErrorResponse);
    }

    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException ige) {
        log.error("Illegal Argument Exception = {}", ige.getMessage());
        GlobalErrorResponseDto illegalArgumentExceptionResponse =
                globalService.errorResponse(ige.getMessage());

        return ResponseEntity.status(BAD_REQUEST).body(illegalArgumentExceptionResponse);
    }

    // IllegalStateException
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> illegalArgumentException(IllegalStateException ise) {
        log.error("Illegal State Exception = {}", ise.getMessage());
        GlobalErrorResponseDto illegalArgumentExceptionResponse =
                globalService.errorResponse(ise.getMessage());

        return ResponseEntity.status(BAD_REQUEST).body(illegalArgumentExceptionResponse);
    }


    // MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> maxUploadSizeExceededException(MaxUploadSizeExceededException mse) {
        log.error("MaxUploadSizeExceededException = {}", mse.getMessage());
        GlobalErrorResponseDto maxUploadSizeExceededExceptionResponse =
                globalService.errorResponse(mse.getMessage());

        return ResponseEntity.status(BAD_REQUEST).body(maxUploadSizeExceededExceptionResponse);
    }

    // AsyncRequestTimeoutException
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    @ResponseBody
    public SseEmitter asyncRequestTimeoutException(AsyncRequestTimeoutException ate) {
        log.error("AsyncRequestTimeoutException = {}", ate.getMessage());
        return null;
    }

    // Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception e) {
        log.error("Exception = {}", e.getMessage());
        GlobalErrorResponseDto exceptionResponse =
                globalService.errorResponse(e.getMessage());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

}
