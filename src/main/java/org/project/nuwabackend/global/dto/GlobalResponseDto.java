package org.project.nuwabackend.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL) // DTO 를 JSON으로 변환 시 null값인 field 제외
public record GlobalResponseDto<Data>(String status, String message,
                                      Data data) {

    @Builder
    public GlobalResponseDto {
    }
}

