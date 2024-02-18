package org.project.nuwabackend.type;

import lombok.Getter;

@Getter
public enum S3PathType {
    IMAGE_PATH("/image"),
    FILE_PATH("/file");

    private final String type;

    S3PathType(String type) {
        this.type = type;
    }
}
