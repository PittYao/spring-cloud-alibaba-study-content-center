package com.fanyao.alibaba.contentcenter;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.fanyao.alibaba.contentcenter.dao.share.ShareMapper;
import com.fanyao.alibaba.contentcenter.domain.dto.user.UserDTO;
import com.fanyao.alibaba.contentcenter.domain.entity.share.Share;
import com.fanyao.alibaba.contentcenter.feignclient.TestFeignOutRibbonClient;
import com.fanyao.alibaba.contentcenter.feignclient.TestUserCenterFeignClient;
import com.fanyao.alibaba.contentcenter.sentineltest.TestSentinelBlockHandlerClass;
import com.fanyao.alibaba.contentcenter.sentineltest.TestSentinelFallBackHandlerClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: bugProvider
 * @date: 2019/12/20 11:16
 * @description:
 */
@Slf4j
@RestController
// 减去mapper警告
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    private final ShareMapper shareMapper;
    private final DiscoveryClient discoveryClient;
    private final TestUserCenterFeignClient testUserCenterFeignClient;
    private final TestFeignOutRibbonClient testFeignOutRibbonClient;


    @GetMapping("test")
    public List<Share> testInsert() {
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
     *
     * @return 服务的所有实例列表
     */
    @GetMapping("/test2")
    public List<ServiceInstance> setDiscoveryClient() {
        // 查询content-center的实例列表
        // consul/zookeeper/eureka 等都可以用discoveryClient
        return this.discoveryClient.getInstances("user-center");
    }

    /**
     * 测试feign GET 请求
     */
    @GetMapping("/test-get")
    public UserDTO query(UserDTO userDTO) {
        return testUserCenterFeignClient.query(userDTO);
    }

    /**
     * 测试feign GET 请求
     */
    @GetMapping("/test-get/common")
    public UserDTO queryByCommon(UserDTO userDTO) {
        return testUserCenterFeignClient.query(userDTO.getId(), userDTO.getWxId());
    }

    /**
     * 测试feign POST 请求
     */
    @PostMapping("/test-post")
    public UserDTO post(UserDTO userDTO) {
        return testUserCenterFeignClient.post(userDTO);
    }

    /**
     * 测试feign 脱离ribbon单独使用
     */
    @GetMapping("/baidu")
    public String feignOutRibbon() {
        return testFeignOutRibbonClient.index();
    }

    /**
     * 测试代码配置 api流控规则
     */
    @GetMapping("/test-add-flow-rule")
    public String addFlowRule() {
        this.initFlowQpsRule();
        return "ADD SUCCESS";
    }

    private void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        // 对指定api配置
        FlowRule rule = new FlowRule("/shares/1");
        // set limit qps to 20
        rule.setCount(20);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    /**
     * @SentinelResource 的实现
     * 测试弃用默认sentinel降级,流控 用代码定义 api规则
     * yml 中关闭 filter
     * sentinel:
     * filter:
     * # 关闭对spring 端点保护
     * enabled: false
     */
    @GetMapping("/test-sentinel-api")
    public String testSentinelRule(@RequestParam(required = false) String b) {
        Entry entry = null;

        // 指定api
        String resourceName = "test-sentinel-api";
        // 指定调用该api的 服务来源
        ContextUtil.enter(resourceName, "test-wfw");

        try {
            // SphU 定义资源 保护资源
            entry = SphU.entry(resourceName);
            if (StringUtils.isBlank(b)) {
                throw new IllegalArgumentException("参数不能为空");
            }
        } catch (BlockException e) {
            // 当 满足流控 和 降级时 会抛出BlockException, sentinel只对BlockException进行统计
            log.warn("限流 或 降级了", e);
            return "限流 或 降级了";
        } catch (IllegalArgumentException e2) {
            // Tracer 要对自己的异常也使用sentinel, 需自己将异常追踪到sentinel
            Tracer.trace(e2);
            return "参数异常";
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }

        return "SUCCESS";
    }

    /**
     * @SentinelResource 的使用
     * - blockHandler 流控或降级处理方法
     * - fallback 处理降级处理方法
     * - 处理方法 和 资源方法 必须相同的方法签名
     */
    @GetMapping("/test-sentinel-resource-api")
    @SentinelResource(
            value = "test-sentinel-resource-api",
            blockHandler = "block",
            blockHandlerClass = TestSentinelBlockHandlerClass.class,
            fallback = "fallback",
            fallbackClass = TestSentinelFallBackHandlerClass.class
    )
    public String testSentinelResource(@RequestParam(required = false) String b) {
        if (StringUtils.isBlank(b)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return "SUCCESS";
    }
}
