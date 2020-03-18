package com.fanyao.alibaba.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: bugProvider
 * @date: 2020/1/14 13:46
 * @description: sentinel的针对来源的实现
 * - 使用场景：
 *      - 不让一些微服务访问我的api（origin==服务名到sentinel的授权规则的黑名单中）
 *      - api流控规则、降级规则中区分来源
 */
//@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        // TODO 在实际项目中 针对来源的参数 应放到Header中
        String origin = request.getHeader("origin");
        if (Strings.isBlank(origin)) {
            throw new IllegalArgumentException("请求参数必须指定origin");
        }
        return origin;
    }
}
