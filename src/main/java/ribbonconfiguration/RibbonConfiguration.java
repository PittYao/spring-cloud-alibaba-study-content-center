package ribbonconfiguration;

import com.fanyao.alibaba.contentcenter.configuration.NacosWeightRule;
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
        return new NacosWeightRule();
//        return new RandomRule();
    }
}
