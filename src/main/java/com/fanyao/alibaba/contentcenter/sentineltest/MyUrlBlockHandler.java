package com.fanyao.alibaba.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: bugProvider
 * @date: 2020/1/14 11:11
 * @description: 处理blockHandler
 * - 流控 和 降级 都会抛出blockException，报相同异常
 * - UrlBlockHandler可用于 区分出 流控或降级异常
 */
@Component
public class MyUrlBlockHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
        ResponseMsg responseMsg = null;
        if (e instanceof FlowException) {
            responseMsg = ResponseMsg.builder()
                    .code(100)
                    .msg("被限流")
                    .build();
        } else if (e instanceof DegradeException) {
            responseMsg = ResponseMsg.builder()
                    .code(101)
                    .msg("被降级")
                    .build();
        } else if (e instanceof SystemBlockException) {
            responseMsg = ResponseMsg.builder()
                    .code(102)
                    .msg("系统规则不满足")
                    .build();
        } else if (e instanceof ParamFlowException) {
            responseMsg = ResponseMsg.builder()
                    .code(103)
                    .msg("热点参数限流")
                    .build();
        } else if (e instanceof AuthorityException) {
            responseMsg = ResponseMsg.builder()
                    .code(104)
                    .msg("授权规则不通过")
                    .build();
        }
        // 构建httpServletResponse
        httpServletResponse.setStatus(500);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type","application/json;charset=utf-8");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        // json写入响应体
        new ObjectMapper()
                .writeValue(
                        httpServletResponse.getWriter(),
                        responseMsg
                );

    }
}

@Data
@Builder
class ResponseMsg {
    private Integer code;
    private String msg;
}