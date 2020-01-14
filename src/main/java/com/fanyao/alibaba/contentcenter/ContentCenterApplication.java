package com.fanyao.alibaba.contentcenter;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.fanyao.alibaba.contentcenter")
@SpringBootApplication
// defaultConfiguration 指定全局feign日志级别
// @EnableFeignClients(defaultConfiguration = GlobalFeignConfiguration.class)
// @EnableFeignClients() 一定要加()
@EnableFeignClients()
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }

    @Bean
    // @LoadBalanced RestTemplate 使用ribbon
    @LoadBalanced
    // @SentinelRestTemplate RestTemplate 使用Sentinel
    @SentinelRestTemplate
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
