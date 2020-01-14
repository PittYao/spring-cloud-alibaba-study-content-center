package com.fanyao.alibaba.contentcenter.feignclient;

import com.fanyao.alibaba.contentcenter.domain.dto.user.UserDTO;
import com.fanyao.alibaba.contentcenter.feignclient.fallback.UserCenterFeignClientFallBack;
import com.fanyao.alibaba.contentcenter.feignclient.fallbackFactory.UserCenterFeignClientFallBackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: bugProvider
 * @date: 2019/12/23 16:27
 * @description:
 */
// 熔断会进入fallback的类
// - fallback 拿不到异常
// - fallbackFactory  能拿到异常
// - fallback 和 fallbackFactory不能同时使用
//@FeignClient(name = "user-center",fallback = UserCenterFeignClientFallBack.class)
@FeignClient(name = "user-center",fallbackFactory = UserCenterFeignClientFallBackFactory.class)
//@FeignClient(name = "user-center",configuration = GlobalFeignConfiguration.class)
public interface UserCenterFeignClient {

    /** 
     * http://user-center/users/{id}
     */
    @GetMapping("users/{id}")
    UserDTO findByUserId(@PathVariable Integer id);


}
