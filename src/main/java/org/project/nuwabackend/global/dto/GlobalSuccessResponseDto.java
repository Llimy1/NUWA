package org.project.nuwabackend.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Objects;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // DTO 를 JSON으로 변환 시 null값인 field 제외
public record GlobalSuccessResponseDto<Data>(String status, String message, Data data) {
}