package org.project.nuwabackend.global.exception;

import org.project.nuwabackend.global.type.ErrorMessage;

public class JwtException extends RuntimeException {
    public JwtException(ErrorMessage message) {
        super(message.getMessage());
    }
}
