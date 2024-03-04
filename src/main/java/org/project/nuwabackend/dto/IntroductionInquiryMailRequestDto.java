package org.project.nuwabackend.dto;

public record IntroductionInquiryMailRequestDto(
        String name, String phoneNumber,
        String email, String countryRegion,
        String companyName, String departmentName,
        String position, String numberOfPeople, String content) {
}
