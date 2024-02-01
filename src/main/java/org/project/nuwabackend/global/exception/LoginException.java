package org.project.nuwabackend.global.exception;


import org.project.nuwabackend.global.type.ErrorMessage;

public class LoginException extends RuntimeException {
    public LoginException(ErrorMessage message) {
        super(message.getMessage());
    }
}
