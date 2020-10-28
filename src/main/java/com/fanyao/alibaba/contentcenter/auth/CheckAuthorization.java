package com.fanyao.alibaba.contentcenter.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: bugProvider
 * @date: 2020/10/28 15:36
 * @description: 权限校验
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAuthorization {
    String value();
}
