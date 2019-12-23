package com.fanyao.alibaba.contentcenter.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @author: bugProvider
 * @date: 2019/12/23 13:45
 * @description: 整合nacos实例的权重配置
 * 权重使用场景：
 * - 把性能差的机器权重设低，性能好的机器权重设高，让请求优先打到性能高的机器上
 * - 某个实例出现异常时，把权重设低，排查问题，问题排查完再把权重恢复
 * - 想要下线某个实例时，可先将该实例的权重设为0，这样流量就不会打到该实例上——此时再去关停该实例，这样就能实现优雅下线
 */
@Slf4j
@NoArgsConstructor
public class NacosWeightRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // 读取配置文件初始化rule
    }

    @Override
    public Server choose(Object o) {
        try {
            DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) this.getLoadBalancer();

            // 请求的服务名
            String name = loadBalancer.getName();

            // 实现权重算法 = nacos client 提供
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            Instance instance = namingService.selectOneHealthyInstance(name);

            if (Objects.isNull(instance)) {
                log.error("当前没有可用的服务实例");
                return null;
            }

            log.info("选择实例：port = {},instance = {}", instance.getPort(), instance.toString());
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("选择实例异常:", e);
            return null;
        }
    }
}
