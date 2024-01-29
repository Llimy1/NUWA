package org.project.nuwabackend.global.type;

import lombok.Getter;

@Getter
public enum GlobalResponseStatus {

    SUCCESS("success"),
    FAIL("fail");

    private final String value;

    GlobalResponseStatus(String value) {
        this.value = value;
    }
}
