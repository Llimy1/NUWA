package org.project.nuwabackend.global.exception.custom;

import org.project.nuwabackend.global.response.type.ErrorMessage;

public class JwtException extends RuntimeException {
    public JwtException(ErrorMessage message) {
        super(message.getMessage());
    }
}
