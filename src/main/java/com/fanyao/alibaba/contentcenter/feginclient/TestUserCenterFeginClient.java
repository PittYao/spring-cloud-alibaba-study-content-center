package com.fanyao.alibaba.contentcenter.feginclient;

import com.fanyao.alibaba.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

/**
 * @author: bugProvider
 * @date: 2019/12/23 16:27
 * @description:
 */
@FeignClient(name = "user-center")
//@FeignClient(name = "user-center",configuration = GlobalFeignConfiguration.class)
public interface TestUserCenterFeginClient {
    /**
     * http://user-center/users/q?id=xxx&wxid=xxx&...
     * 不支持feign继承
     */
    @GetMapping("users/q")
    UserDTO query(@SpringQueryMap UserDTO userDTO);

    /**
     * http://user-center/users/q?id=xxx&wxid=xxx&...
     * 支持feign继承
     */
    @GetMapping("users/q")
    UserDTO query(@RequestParam("id") Integer id,@RequestParam("wxId") String wxId);
}
