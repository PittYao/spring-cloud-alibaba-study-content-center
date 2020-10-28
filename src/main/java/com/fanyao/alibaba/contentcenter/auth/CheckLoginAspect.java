package com.fanyao.alibaba.contentcenter.auth;

import com.fanyao.alibaba.contentcenter.security.SecurityException;
import com.fanyao.alibaba.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;


/**
 * @author: bugProvider
 * @date: 2020/10/27 14:35
 * @description: Aop 验证是否登录
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CheckLoginAspect {
    private final JwtOperator jwtOperator;

    /**
     * 校验是否携带token和token合法性
     */
    @Around("@annotation(com.fanyao.alibaba.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {
        checkToken();
        return point.proceed();
    }


    /**
     * 校验token权限
     */
    @Around("@annotation(com.fanyao.alibaba.contentcenter.auth.CheckAuthorization)")
    public Object checkToken(ProceedingJoinPoint point) throws Throwable {
        // 1. 验证token是否合法
        this.checkToken();
        // 2. 验证角色是否匹配
        HttpServletRequest request = this.getHttpServletRequest();
        String role = (String) request.getAttribute("role");

        // 3. 比较role和 注解中的value值 判断是否匹配

        // 3.1 拿到注解中的角色值
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);

        String value = annotation.value();

        // TODO 只有单个角色信息
        if (!Objects.equals(role, value)) {
            throw new SecurityException("用户无权访问");
        }

        return point.proceed();

    }


    /**
     * 从request中获取token，并验证合法性
     */
    private void checkToken() {
        try {
            // 1. header中是否有token
            HttpServletRequest request = getHttpServletRequest();

            String token = request.getHeader("Token");

            if (Strings.isBlank(token)) {
                // 抛异常
                log.warn("请求头未携带Token");
                throw new SecurityException("请求头未携带Token");
            }

            // 2. 检验token是否合法，不合法抛异常
            Boolean validateToken = jwtOperator.validateToken(token);
            if (!validateToken) {
                log.warn("Token检验不合法");
                throw new SecurityException("Token不合法");
            }

            // 3. 校验成功，将用户信息设置到request的attribute里
            Claims claims = jwtOperator.getClaimsFromToken(token);

            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickName", claims.get("wxNickName"));
            request.setAttribute("role", claims.get("role"));

        } catch (Throwable throwable) {
            throw new SecurityException("用户无权访问", throwable);
        }
    }

    /**
     * 获取request对象
     */
    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        assert servletRequestAttributes != null;
        return servletRequestAttributes.getRequest();
    }
}
