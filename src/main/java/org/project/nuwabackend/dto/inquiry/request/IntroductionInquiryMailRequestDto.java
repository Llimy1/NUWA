package org.project.nuwabackend.dto.inquiry.request;

public record IntroductionInquiryMailRequestDto(
        String name, String phoneNumber,
        String email, String countryRegion,
        String companyName, String departmentName,
        String position, String numberOfPeople, String content) {
}
