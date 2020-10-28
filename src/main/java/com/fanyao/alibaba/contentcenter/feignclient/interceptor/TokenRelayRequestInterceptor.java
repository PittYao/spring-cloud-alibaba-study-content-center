package com.fanyao.alibaba.contentcenter.feignclient.interceptor;

import com.fanyao.alibaba.contentcenter.security.SecurityException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: bugProvider
 * @date: 2020/10/28 14:29
 * @description: fegin间传递token的拦截器
 */
@Slf4j
public class TokenRelayRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 1.获取token
        // 1. header中是否有token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();

        String token = request.getHeader("Token");

        if (Strings.isNotBlank(token)) {
            // 2.传递token
            requestTemplate.header("Token", token);
        }

    }
}
