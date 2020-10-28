package com.fanyao.alibaba.contentcenter.security;

import lombok.AllArgsConstructor;
import lombok.Builder;


/**
 * @author: bugProvider
 * @date: 2020/10/27 14:42
 * @description:
 */

@AllArgsConstructor
@Builder
public class SecurityException extends RuntimeException {

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable e) {
        super(message, e);
    }
}
