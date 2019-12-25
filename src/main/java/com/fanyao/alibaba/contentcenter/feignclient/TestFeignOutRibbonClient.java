package com.fanyao.alibaba.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: bugProvider
 * @date: 2019/12/25 11:34
 * @description: @FeignClient必须写 name 或者 value属性
 */
@FeignClient(name = "baidu",url = "http://www.baidu.com")
public interface TestFeignOutRibbonClient {
    @GetMapping("")
    String index();
}
