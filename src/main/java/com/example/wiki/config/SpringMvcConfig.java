package com.example.wiki.config;

import com.example.wiki.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    //首先把拦截器注入进来
    @Resource
    private LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器，添加规则，去除规则
        registry.addInterceptor(logInterceptor).addPathPatterns("/**").excludePathPatterns("/login");
    }
}