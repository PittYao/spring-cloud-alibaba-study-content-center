package com.fanyao.alibaba.contentcenter;

import com.fanyao.alibaba.contentcenter.dao.share.ShareMapper;
import com.fanyao.alibaba.contentcenter.domain.entity.share.Share;
import com.fanyao.alibaba.contentcenter.domain.enums.AuditStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContentCenterApplicationTests {

    @Autowired
    private ShareMapper shareMapper;

    @Test
    void contextLoads() {
    }

    @Test
    public void test() {
        Share share = Share.builder()
                .id(2)
                .auditStatus(AuditStatusEnum.PASS.toString())
                .reason("测试")
                .build();

        this.shareMapper.updateByPrimaryKeySelective(share);
    }
}
