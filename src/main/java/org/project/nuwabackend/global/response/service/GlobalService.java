package org.project.nuwabackend.global.response.service;

import org.project.nuwabackend.global.response.dto.GlobalErrorResponseDto;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.springframework.stereotype.Service;

import static org.project.nuwabackend.global.response.type.GlobalResponseStatus.*;

@Service
public class GlobalService {

    public GlobalSuccessResponseDto<Object> successResponse(String message, Object data) {
        return GlobalSuccessResponseDto.builder()
                .status(SUCCESS.getValue())
                .message(message)
                .data(data)
                .build();
    }

    public GlobalErrorResponseDto errorResponse(String message) {
        return GlobalErrorResponseDto.builder()
                .status(FAIL.getValue())
                .message(message)
                .build();
    }
}