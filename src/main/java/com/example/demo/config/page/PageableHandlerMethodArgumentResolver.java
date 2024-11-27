package com.example.demo.config.page;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final int DEFAULT_OFFSET = 0;

    private static final int DEFAULT_LIMIT = 5;

    private final String offsetParam;

    private final String limitParam;

    public PageableHandlerMethodArgumentResolver() {
        this("offset", "limit");
    }

    public PageableHandlerMethodArgumentResolver(String offsetParam, String limitParam) {
        this.offsetParam = offsetParam;
        this.limitParam = limitParam;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String offsetString = webRequest.getParameter(offsetParam);
        String limitString = webRequest.getParameter(limitParam);

        int offset = toInt(offsetString, DEFAULT_OFFSET);
        int limit = toInt(limitString, DEFAULT_LIMIT);

        if (offset < 0) {
            offset = DEFAULT_OFFSET;
        }
        if (limit < 1 || limit > 5) {
            limit = DEFAULT_LIMIT;
        }

        return PageRequest.of(offset, limit);
    }

}
