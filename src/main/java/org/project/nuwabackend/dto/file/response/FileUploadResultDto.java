package org.project.nuwabackend.dto.file.response;

import org.project.nuwabackend.type.S3PathType;

import java.util.List;

public record FileUploadResultDto(List<String> uploadImageUrlList, List<String> uploadFileUrlList) {
}
