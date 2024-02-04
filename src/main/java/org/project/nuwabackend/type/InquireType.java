package org.project.nuwabackend.type;

import lombok.Getter;

@Getter
public enum InquireType {
    SERVICE("service"),
    INTRODUCTION("introduction");

    private final String type;

    InquireType(String type) {
        this.type = type;
    }
}
