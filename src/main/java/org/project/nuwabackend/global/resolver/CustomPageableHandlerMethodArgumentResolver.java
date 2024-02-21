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
        CustomPageable customPageable = parameter.getParameterAnnotation(CustomPageable.class);

        String pageStr = webRequest.getParameter("page");
        String sizeStr = webRequest.getParameter("size");
        String sortBy;

        if (webRequest.getParameter("sortBy") != null) {
            sortBy = webRequest.getParameter("sortBy");
        } else {
            assert customPageable != null;
            sortBy = customPageable.sortBy();
        }

        int page;
        if (pageStr != null) {
            page = Integer.parseInt(pageStr);
        } else {
            assert customPageable != null;
            page = customPageable.page();
        }

        int size;
        if (sizeStr != null) {
            size = Integer.parseInt(sizeStr);
        } else {
            assert customPageable != null;
            size = customPageable.size();
        }

        return PageRequest.of(page, size, Sort.by(sortBy));
    }
}
