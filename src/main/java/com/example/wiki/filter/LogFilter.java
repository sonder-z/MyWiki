package com.example.wiki.filter;

import org.jcp.xml.dsig.internal.dom.ApacheTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class LogFilter implements Filter {
    // 添加日志打印
    private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 重写过滤器所要做的事，打印请求信息
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        LOG.info("------------logFilter开始------------");
        LOG.info("请求地址：{} {}",request.getRequestURL().toString(),request.getMethod());
        LOG.info("远程地址：{}", request.getRemoteAddr());

        //打印接口耗时
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(servletRequest,servletResponse);
        LOG.info("-----------LogFilter结束 耗时:{}ms--------------", System.currentTimeMillis() - startTime);
    }
}
