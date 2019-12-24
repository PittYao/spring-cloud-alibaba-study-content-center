package com.fanyao.alibaba.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @author: bugProvider
 * @date: 2019/12/24 15:50
 * @description: fegin 日志级别
 * 一定不要加@Configuration
 */
public class GlobalFeignConfiguration {

    @Bean
    public Logger.Level level() {
//        Logger.Level.NONE;
//        Logger.Level.BASIC;
//        Logger.Level.HEADERS;
        return Logger.Level.FULL;
    }
}
