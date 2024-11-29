package com.example.demo.config.page;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final String sortParam;

    public PageableHandlerMethodArgumentResolver() {
        this("offset", "limit","sort");
    }

    public PageableHandlerMethodArgumentResolver(String offsetParam, String limitParam, String sortParam) {
        this.offsetParam = offsetParam;
        this.limitParam = limitParam;
        this.sortParam = sortParam;
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


        String sortString = webRequest.getParameter(sortParam);
        Sort sort = null;
        if (sortString != null && !sortString.isEmpty()) {
            String[] sortParams = sortString.split(",");
            if (sortParams.length > 0) {
                String property = sortParams[0];
                Sort.Order order = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]))
                        ? Sort.Order.desc(property)
                        : Sort.Order.asc(property);
                sort = Sort.by(order);
            }
        }

        return (sort != null) ? PageRequest.of(offset, limit, sort) : PageRequest.of(offset, limit);
    }

}
