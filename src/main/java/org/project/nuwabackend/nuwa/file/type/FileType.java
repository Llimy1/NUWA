package org.project.nuwabackend.nuwa.file.type;

import lombok.Getter;

@Getter
public enum FileType {
    CANVAS("canvas"),
    DIRECT("direct"),
    CHAT("chat"),
    VOICE("voice");

    private final String value;

    FileType(String value) {
        this.value = value;
    }
}
