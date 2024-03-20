package org.project.nuwabackend.global.exception.custom;


import org.project.nuwabackend.global.response.type.ErrorMessage;

public class DuplicationException extends RuntimeException {
    public DuplicationException(ErrorMessage message) {
        super(message.getMessage());
    }
}
