package org.project.nuwabackend.global.resolver;

import org.project.nuwabackend.global.annotation.CustomPageable;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CustomPageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CustomPageable.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        CustomPageable customPageable = parameter.getMethodAnnotation(CustomPageable.class);

        int page = customPageable.page() - 1;
        int size = customPageable.size();
        String sortBy = customPageable.sortBy();

        return PageRequest.of(page, size, Sort.by(sortBy));
    }
}
