package org.project.nuwabackend.dto;

import org.springframework.web.multipart.MultipartFile;

public record ServiceInquiryMailRequestDto(
        String subject, // 문의 주제
        String email,
        String content,
        MultipartFile[] attachments // 파일 첨부를 위한 추가 필드
        ) {
}
