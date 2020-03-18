package com.fanyao.alibaba.contentcenter.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditStatusEnum {
    /** 未审核 */
    NOT_YET,
    /** 通过审核 */
    PASS,
    /** 拒绝 */
    REJECT
}
