package org.project.nuwabackend.global.exception.custom;

import org.springframework.security.core.AuthenticationException;

public class OAuth2Exception extends AuthenticationException {
    public OAuth2Exception(String msg) {
        super(msg);
    }
}
