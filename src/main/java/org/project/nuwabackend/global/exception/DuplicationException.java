package org.project.nuwabackend.global.exception;


import org.project.nuwabackend.global.type.ErrorMessage;

public class DuplicationException extends RuntimeException {
    public DuplicationException(ErrorMessage message) {
        super(message.getMessage());
    }
}
