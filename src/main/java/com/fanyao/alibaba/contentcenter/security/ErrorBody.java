package com.fanyao.alibaba.contentcenter.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: bugProvider
 * @date: 2020/10/27 15:07
 * @description: 异常返回响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorBody {
    private String body;
    private int status;
}
