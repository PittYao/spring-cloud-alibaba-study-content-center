package com.fanyao.alibaba.contentcenter;

import org.springframework.web.client.RestTemplate;

/**
 * @author: bugProvider
 * @date: 2019/12/25 16:53
 * @description:
 */
public class SentinelTests {
    public static void main(String[] args) throws InterruptedException {
        // 模拟Sentinel关联 限流措施
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 1000; i++) {
            restTemplate.getForObject("http://127.0.0.1:8050/actuator/sentinel", String.class);
            Thread.sleep(500);
        }
    }
}
