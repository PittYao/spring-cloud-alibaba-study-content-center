package com.fanyao.alibaba.contentcenter.domain.dto.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: bugProvider
 * @date: 2020/3/18 14:20
 * @description: 用户添加积分MQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddBonusMsgDTO {
    // 用户id
    private Integer userId;
    // 积分
    private Integer bonus;
}
