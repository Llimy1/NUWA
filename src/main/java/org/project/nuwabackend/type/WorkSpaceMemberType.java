package org.project.nuwabackend.type;

import lombok.Getter;

@Getter
public enum WorkSpaceMemberType {

    CREATED("create"),
    JOIN("join");

    private final String type;

    WorkSpaceMemberType(String type) {
        this.type = type;
    }
}
