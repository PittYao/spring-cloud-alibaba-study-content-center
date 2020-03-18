package com.fanyao.alibaba.contentcenter.controller;

import com.fanyao.alibaba.contentcenter.domain.dto.share.ShareAuditDTO;
import com.fanyao.alibaba.contentcenter.domain.entity.share.Share;
import com.fanyao.alibaba.contentcenter.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: bugProvider
 * @date: 2020/1/14 14:30
 * @description:
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/admin/shares")
public class ShareAdminController {
    private final ShareService shareService;
    /** 
     * 审核指定内容
     * @param id	
     * @param shareAuditDTO
     */
    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id, ShareAuditDTO shareAuditDTO){
        // TODO 权限校验
        return shareService.auditById(id, shareAuditDTO);
    }
}
