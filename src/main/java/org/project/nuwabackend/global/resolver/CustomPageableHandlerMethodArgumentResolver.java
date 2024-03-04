package org.project.nuwabackend.global.resolver;

import org.project.nuwabackend.global.annotation.CustomPageable;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
        String sortOrder;

        assert customPageable != null;

        if (webRequest.getParameter("sortBy") != null) {
            sortBy = webRequest.getParameter("sortBy");
        } else {
            sortBy = customPageable.sortBy();
        }

        int page;
        if (pageStr != null) {
            page = Integer.parseInt(pageStr);
        } else {
            page = customPageable.page();
        }

        int size;
        if (sizeStr != null) {
            size = Integer.parseInt(sizeStr);
        } else {
            size = customPageable.size();
        }

        if (webRequest.getParameter("sortOrder") != null) {
            sortOrder = webRequest.getParameter("sortOrder");
        } else {
            sortOrder = customPageable.sortOrder();
        }

        assert sortOrder != null;
        Direction order = sortOrder.equals("desc") ? Direction.DESC : Direction.ASC;

        return PageRequest.of(page, size, Sort.by(order, sortBy));
    }
}
