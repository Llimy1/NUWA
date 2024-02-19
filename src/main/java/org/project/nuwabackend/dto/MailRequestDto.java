package org.project.nuwabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;





public record MailRequestDto(

String name, String phoneNumber,
 String email, String countryRegion,
 String companyName, String departmentName,
 String position, String numberOfPeople, String content) {

}

