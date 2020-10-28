package com.fanyao.alibaba.contentcenter.controller;

import com.fanyao.alibaba.contentcenter.auth.CheckLogin;
import com.fanyao.alibaba.contentcenter.domain.dto.share.ShareDTO;
import com.fanyao.alibaba.contentcenter.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: bugProvider
 * @date: 2019/12/20 14:23
 * @description:
 */
@RestController
@RequestMapping("/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {
    private final ShareService shareService;

    @GetMapping("/{id}")
    public ShareDTO findById(@PathVariable Integer id) {
        return shareService.findById(id);
    }

    @GetMapping("/ribbon/{id}")
    public ShareDTO findByIdByRibbon(@PathVariable Integer id) {
        return shareService.findByIdRibbon(id);
    }

    // 接收header 中的 token
    @GetMapping("/feign/{id}")
    @CheckLogin
    public ShareDTO findByIdByFeign(@PathVariable Integer id) {
        return shareService.findByIdByFeign(id);
    }


}
