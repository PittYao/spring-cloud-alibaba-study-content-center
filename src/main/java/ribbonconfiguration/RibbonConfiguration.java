package ribbonconfiguration;

import com.fanyao.alibaba.contentcenter.configuration.NacosSameClusterWeightRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: bugProvider
 * @date: 2019/12/23 11:22
 * @description: ribbon包在新包中建立 避免父子上下文冲突（冲突后事务失效）
 */
@Configuration
public class RibbonConfiguration {

    @Bean
    public IRule ribbonRule() {
        // 自定义nacos负载均衡规则
        return new NacosSameClusterWeightRule();
        // 自定义nacos权重负载均衡规则
//      return new NacosWeightRule();
        // 自带随机访问负载均衡规则
//      return new RandomRule();
    }
}
