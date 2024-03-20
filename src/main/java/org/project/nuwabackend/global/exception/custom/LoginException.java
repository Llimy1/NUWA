package org.project.nuwabackend.global.exception.custom;


import org.project.nuwabackend.global.response.type.ErrorMessage;

public class LoginException extends RuntimeException {
    public LoginException(ErrorMessage message) {
        super(message.getMessage());
    }
}
