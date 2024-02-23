package org.project.nuwabackend.type;

import lombok.Getter;

@Getter
public enum NotificationType {

    DIRECT("direct"),
    CHAT("chat"),
    VOICE("voice");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }
}
