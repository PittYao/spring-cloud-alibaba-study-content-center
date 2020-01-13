package com.fanyao.alibaba.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: bugProvider
 * @date: 2020/1/13 15:35
 * @description: Sentinel流控 异常处理类
 */
@Slf4j
public class TestSentinelBlockHandlerClass {
    /**
     * 处理 流控或降级
     * - 必须加上 static
     */
    public static String block(String b, BlockException e) {
        log.warn("限流 或 降级", e);
        return "限流 或 降级了 block";
    }
}
