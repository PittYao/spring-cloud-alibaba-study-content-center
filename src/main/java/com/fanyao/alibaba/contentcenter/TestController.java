package com.fanyao.alibaba.contentcenter;
import java.util.Date;
import java.util.List;

import com.fanyao.alibaba.contentcenter.dao.share.ShareMapper;
import com.fanyao.alibaba.contentcenter.domain.entity.share.Share;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: bugProvider
 * @date: 2019/12/20 11:16
 * @description:
 */
@RestController
// 减去mapper警告
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    private final ShareMapper shareMapper;
    private final DiscoveryClient discoveryClient;

    @GetMapping("test")
    public List<Share> testInsert(){
        Share share = new Share();
        share.setUserId(0);
        share.setTitle("");
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setIsOriginal(false);
        share.setAuthor("");
        share.setCover("");
        share.setSummary("");
        share.setPrice(0);
        share.setDownloadUrl("");
        share.setBuyCount(0);
        share.setShowFlag(false);
        share.setAuditStatus("");
        share.setReason("");

        shareMapper.insertSelective(share);

        return shareMapper.selectAll();
    }

    /** 
     * 测试从注册中心做服务发现
     * @return 服务的所有实例列表
     */
    @GetMapping("/test2")
    public List<ServiceInstance> setDiscoveryClient(){
        // 查询content-center的实例列表
        // consul/zookeeper/eureka 等都可以用discoveryClient
        return this.discoveryClient.getInstances("user-center");
    }
}
