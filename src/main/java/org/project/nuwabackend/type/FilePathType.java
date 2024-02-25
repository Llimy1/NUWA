package org.project.nuwabackend.type;

import lombok.Getter;

@Getter
public enum FilePathType {
    IMAGE_PATH("/image"),
    FILE_PATH("/file");

    private final String type;

    FilePathType(String type) {
        this.type = type;
    }
}
