package com.fanyao.alibaba.contentcenter.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: bugProvider
 * @date: 2019/12/23 13:45
 * @description: 整合nacos实例的优先就近集群调用
 */
@Slf4j
@NoArgsConstructor
public class NacosSameClusterWeightRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // 读取配置文件初始化rule
    }

    @Override
    public Server choose(Object o) {
        // 1. 拿到yml配置文件中的clusterName集群名字
        String clusterName = nacosDiscoveryProperties.getClusterName();

        try {
            // 2. 获取集群的服务列表
            DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) this.getLoadBalancer();

            // 请求的服务名
            String name = loadBalancer.getName();

            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            List<Instance> instances = namingService.selectInstances(name, true);
            if (CollectionUtils.isEmpty(instances)) {
                log.error("当前没有可用的 {} 服务实例",name);
                return null;
            }
            
            // 3. 过滤出和配置中同一集群的实例
            List<Instance> sameCluterInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());

            // 4. 如果同集群没有可用实例 调用其他集群实例
            List<Instance> chooseInstances;
            if (CollectionUtils.isEmpty(sameCluterInstances)) {
                chooseInstances = instances;
                log.warn("选择实例时,出现跨集群调用,name = {},chooseInstances = {}",
                        name,
                        chooseInstances.toString()
                );
            }else {
                chooseInstances = sameCluterInstances;
            }

            // 5.从实例列表中 根据权重 选出一个实例
            Instance chooseInstance = ExtendBalancer.getHostByRandomWeightExtend(chooseInstances);
            log.info("选择实例：port = {},instance = {}", chooseInstance.getPort(), chooseInstance.toString());
            
            return new NacosServer(chooseInstance);
        } catch (NacosException e) {
            log.error("选择实例异常:", e);
            return null;
        }
    }
}

class ExtendBalancer extends Balancer {

    /**
     * Return one host from the host list by random-weight.
     *
     * @param hosts The list of the host.
     * @return The random-weight result of the host
     */
    public static Instance getHostByRandomWeightExtend(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}
