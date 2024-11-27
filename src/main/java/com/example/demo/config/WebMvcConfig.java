package com.example.demo.config;

import com.example.demo.config.page.PageableHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public PageableHandlerMethodArgumentResolver simpleOffsetPageableHandlerMethodArgumentResolver() {
        return new PageableHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(simpleOffsetPageableHandlerMethodArgumentResolver());
    }
}
