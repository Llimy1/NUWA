package org.project.nuwabackend.dto.file.response;

import java.util.Map;

public record FileUploadResultDto(Map<String, Long> uploadImageUrlList, Map<String, Long> uploadFileUrlList) {
}
