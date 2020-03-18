package com.fanyao.alibaba.contentcenter.sentineltest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: bugProvider
 * @date: 2020/1/13 15:38
 * @description: 降级处理类
 */
@Slf4j
public class TestSentinelFallBackHandlerClass {
    /**
     * - 处理降级
     * - 资源方法抛出异常 都会进入fallback
     */
    public static String fallback(String b, Throwable throwable) {
        log.warn("异常进入降级", throwable);
        return "降级了 fallback";
    }
}
