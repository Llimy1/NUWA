package org.project.nuwabackend.global.service;

import org.project.nuwabackend.global.dto.GlobalResponseDto;
import org.springframework.stereotype.Service;

import static org.project.nuwabackend.global.type.GlobalResponseStatus.*;

@Service
public class GlobalService {


    public GlobalResponseDto<Object> successResponse(String message, Object data) {
        return GlobalResponseDto.builder()
                .status(SUCCESS.getValue())
                .message(message)
                .data(data)
                .build();
    }

    public GlobalResponseDto<Object> errorResponse(String message) {
        return GlobalResponseDto.builder()
                .status(FAIL.getValue())
                .message(message)
                .data(null)
                .build();
    }
}