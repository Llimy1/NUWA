package org.project.nuwabackend.nuwa.inquiry.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ServiceInquiryMailRequestDto(
        String subject,
        String email,
        String content
        ) {
}
