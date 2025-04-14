package com.house.rental.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class ApiPrefixFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        log.info("处理请求: {}", uri);
        
        // 如果是静态资源请求，直接放行
        if (uri.startsWith("/uploads/") || uri.equals("/favicon.ico")) {
            log.info("静态资源请求，直接放行: {}", uri);
            chain.doFilter(request, response);
            return;
        }
        
        // 如果URI以/api开头
        if (uri.startsWith("/api/")) {
            log.info("API请求，移除/api前缀: {}", uri);
            // 创建一个新的请求包装器，修改URI
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getRequestURI() {
                    return uri.substring(4); // 移除"/api"
                }
                
                @Override
                public String getServletPath() {
                    return uri.substring(4);
                }
            };
            chain.doFilter(wrapper, response);
        } else {
            log.info("非API请求，直接放行: {}", uri);
            chain.doFilter(request, response);
        }
    }
} 