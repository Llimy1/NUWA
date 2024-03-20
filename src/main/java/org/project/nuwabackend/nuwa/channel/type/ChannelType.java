package org.project.nuwabackend.nuwa.channel.type;

import lombok.Getter;

@Getter
public enum ChannelType {

    CHAT("chat"),
    VOICE("voice"),
    DIRECT("direct");

    private final String type;

    ChannelType(String type) {
        this.type = type;
    }
}
