package com.fanyao.alibaba.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

/**
 * @author: bugProvider
 * @date: 2019/12/23 11:19
 * @description: 指定所有服务的默认ribbon规则，ribbon的<b>全局配置</b>
 * 优先级：全局配置>配置文件
 */
@Configuration
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class DefaultConfiguration {
}
