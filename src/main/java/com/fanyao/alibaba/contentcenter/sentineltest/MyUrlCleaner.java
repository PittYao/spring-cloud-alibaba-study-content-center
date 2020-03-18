package com.fanyao.alibaba.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author: bugProvider
 * @date: 2020/1/14 14:05
 * @description: 实现sentinel 对 restful的支持
 * - sentinel暂不支持restful
 * - UrlCleaner 根据获取的请求的url 返回给sentinel相同的资源名
 * - 如：/shares/1 /shares/2 都返回 /shares/{number}
 */
@Component
public class MyUrlCleaner implements UrlCleaner {
    @Override
    public String clean(String s) {
        String[] split = s.split("/");

        return Arrays.stream(split)
                .map(string -> {
                    if (NumberUtils.isNumber(string)) {
                        return "{number}";
                    }
                    return string;
                })
                .reduce((a, b) -> a + "/" + b)
                .orElse("");
    }
}
