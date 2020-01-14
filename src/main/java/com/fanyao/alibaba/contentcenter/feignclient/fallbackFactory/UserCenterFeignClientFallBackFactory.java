package com.fanyao.alibaba.contentcenter.feignclient.fallbackFactory;

import com.fanyao.alibaba.contentcenter.domain.dto.user.UserDTO;
import com.fanyao.alibaba.contentcenter.feignclient.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: bugProvider
 * @date: 2020/1/13 17:46
 * @description: feign fallback
 */
@Slf4j
@Component
public class UserCenterFeignClientFallBackFactory implements FallbackFactory<UserCenterFeignClient> {

    @Override
    public UserCenterFeignClient create(Throwable throwable) {
        return new UserCenterFeignClient() {
            @Override
            public UserDTO findByUserId(Integer id) {
                log.warn("远程调用被限流/降级了", throwable);
                // 熔断逻辑
                UserDTO userDTO = new UserDTO();
                userDTO.setWxNickname("默认用户");
                return userDTO;
            }
        };
    }
}
