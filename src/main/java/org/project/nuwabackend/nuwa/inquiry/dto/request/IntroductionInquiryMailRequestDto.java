package org.project.nuwabackend.nuwa.inquiry.dto.request;

public record IntroductionInquiryMailRequestDto(
        String name, String phoneNumber,
        String email, String countryRegion,
        String companyName, String departmentName,
        String position, String numberOfPeople, String content) {
}
