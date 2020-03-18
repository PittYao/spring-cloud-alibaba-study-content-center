package com.fanyao.alibaba.contentcenter.domain.dto.share;

import com.fanyao.alibaba.contentcenter.domain.enums.AuditStatusEnum;
import lombok.Data;

/**
 * @author: bugProvider
 * @date: 2020/1/14 14:33
 * @description: 分享审核DTO
 */
@Data
public class ShareAuditDTO {
    /** 审核状态 */
    private AuditStatusEnum auditStatusEnum;
    /** 审核原因 */
    private String reason;
    
}
