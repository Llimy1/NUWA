package org.project.nuwabackend.dto;

import org.springframework.web.multipart.MultipartFile;

public record ServiceInquiryMailRequestDto(
        String subject,
        String email,
        String content
        ) {
}
