package org.project.nuwabackend.global.annotation.resolver;

import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.exception.custom.JwtException;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.project.nuwabackend.global.response.type.ErrorMessage.JWT_EXPIRED;

@RequiredArgsConstructor
public class MemberEmailHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameter().getAnnotation(MemberEmail.class) != null
                && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String accessToken = webRequest.getHeader("Authorization");
        if (!jwtUtil.verifyToken(accessToken)) {
            throw new JwtException(JWT_EXPIRED);
        }
        return jwtUtil.getEmail(accessToken);
    }
}
