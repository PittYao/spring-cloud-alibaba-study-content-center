package com.fanyao.alibaba.contentcenter;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: bugProvider
 * @date: 2019/12/25 17:17
 * @description:
 */
@Slf4j
@Service
public class TestService {

    @SentinelResource("common")
    public void common(){
        log.info("common");
    }
}
