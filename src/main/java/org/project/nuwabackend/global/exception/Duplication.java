package org.project.nuwabackend.global.exception;


import org.project.nuwabackend.global.type.ErrorMessage;

public class Duplication extends RuntimeException {
    public Duplication(ErrorMessage message) {
        super(message.getMessage());
    }
}
