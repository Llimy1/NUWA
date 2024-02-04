package org.project.nuwabackend.global.exception;

import org.project.nuwabackend.global.type.ErrorMessage;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ErrorMessage message) {
        super(message.getMessage());
    }
}
