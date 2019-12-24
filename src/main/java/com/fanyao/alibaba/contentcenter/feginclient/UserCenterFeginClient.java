package com.fanyao.alibaba.contentcenter.feginclient;

import com.fanyao.alibaba.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: bugProvider
 * @date: 2019/12/23 16:27
 * @description:
 */
@FeignClient(name = "user-center")
//@FeignClient(name = "user-center",configuration = GlobalFeignConfiguration.class)
public interface UserCenterFeginClient {
    /** 
     * http://user-center/users/{id}
     */
    @GetMapping("users/{id}")
    UserDTO findByUserId(@PathVariable Integer id);


}
