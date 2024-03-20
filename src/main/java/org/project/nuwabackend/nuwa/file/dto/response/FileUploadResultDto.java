package org.project.nuwabackend.nuwa.file.dto.response;

import java.util.Map;

public record FileUploadResultDto(Map<String, Long> uploadImageUrlMap, Map<String, Long> uploadFileUrlMap) {
}
