package org.project.nuwabackend.global.exception.custom;

import org.project.nuwabackend.global.response.type.ErrorMessage;

public class SseException extends RuntimeException {
    public SseException(String message) {
        super(message);
    }
}
