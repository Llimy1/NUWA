package org.project.nuwabackend.nuwa.auth.type;

import lombok.Getter;

@Getter
public enum Role {

    USER("ROLE_USER");

    private final String key;

    Role(String key) {
        this.key = key;
    }
}
