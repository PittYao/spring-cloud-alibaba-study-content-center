package com.fanyao.alibaba.contentcenter.service;

import com.fanyao.alibaba.contentcenter.dao.rocketmq.RocketMqTransactionMapper;
import com.fanyao.alibaba.contentcenter.dao.share.ShareMapper;
import com.fanyao.alibaba.contentcenter.domain.dto.msg.UserAddBonusMsgDTO;
import com.fanyao.alibaba.contentcenter.domain.dto.share.ShareAuditDTO;
import com.fanyao.alibaba.contentcenter.domain.dto.share.ShareDTO;
import com.fanyao.alibaba.contentcenter.domain.dto.user.UserDTO;
import com.fanyao.alibaba.contentcenter.domain.entity.rocketmq.RocketMqTransaction;
import com.fanyao.alibaba.contentcenter.domain.entity.share.Share;
import com.fanyao.alibaba.contentcenter.domain.enums.AuditStatusEnum;
import com.fanyao.alibaba.contentcenter.feignclient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author: bugProvider
 * @date: 2019/12/20 14:21
 * @description:
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {
    private final ShareMapper shareMapper;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    private final UserCenterFeignClient userCenterFeignClient;
    private final RocketMQTemplate rocketMQTemplate;
    private final RocketMqTransactionMapper rocketMqTransactionMapper;

    // 自定义 随机负载均衡器 消费实例
    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);

        // 发布人id
        Integer userId = share.getUserId();
        // 查询用户服务 发布人信息

        // 服务发现实例列表
        List<ServiceInstance> userInstances = this.discoveryClient.getInstances("user-center");
        if (CollectionUtils.isEmpty(userInstances)) {
            throw new IllegalArgumentException("当前没有可用的服务实例");
        }

        // 构建请求地址
        List<String> targetUrls = userInstances.stream()
                .map(instance -> "http://" + instance.getServiceId() + "/users/{id}")
                .collect(Collectors.toList());

        // 随机选择一个服务实例
        int i = ThreadLocalRandom.current().nextInt(targetUrls.size());
        String targetUrl = targetUrls.get(i);
        log.info("请求服务地址:{}", targetUrl);

        // 请求
        UserDTO userDTO = this.restTemplate.getForObject(
                targetUrl,
                UserDTO.class,
                userId
        );

        // 消息装配
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(Objects.requireNonNull(userDTO).getWxNickname());

        return shareDTO;
    }

    // ribbon负载均衡器 消费实例
    public ShareDTO findByIdRibbon(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);

        // 发布人id
        Integer userId = share.getUserId();
        // 查询用户服务 发布人信息 http://{服务名}/xxx/
        UserDTO userDTO = this.restTemplate.getForObject(
                "http://user-center/users/{id}",
                UserDTO.class,
                userId
        );

        // 消息装配
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(Objects.requireNonNull(userDTO).getWxNickname());

        return shareDTO;
    }

    // fegin消费实例
    public ShareDTO findByIdByFeign(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);

        // 发布人id
        Integer userId = share.getUserId();
        // 查询用户服务 发布人信息
        UserDTO userDTO = userCenterFeignClient.findByUserId(userId);

        // 消息装配
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(Objects.requireNonNull(userDTO).getWxNickname());

        return shareDTO;
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        // GET 调用
        UserDTO userDTO = restTemplate.getForObject(
                "http://127.0.0.1:8041/users/{id}",
                UserDTO.class,
                1
        );

        System.out.println(userDTO);

        // POST 调用
    }


    public Share auditById(Integer id, ShareAuditDTO shareAuditDTO) {
        // 1.只审核未通过的，否则报异常
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (Objects.isNull(share)) {
            throw new IllegalArgumentException("参数非法!该分享不存在");
        }
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法!该分享已通过或拒绝");
        }


        UserAddBonusMsgDTO addBonusMsgDTO = UserAddBonusMsgDTO.builder()
                .userId(share.getUserId())
                .bonus(50)
                .build();

        // 如果是审核通过状态 则发送MQ 加积分
        if (AuditStatusEnum.PASS.equals(shareAuditDTO.getAuditStatusEnum())) {
            // TODO 发送半消息
            String transactionalID = UUID.randomUUID().toString();
            // 构建消息体
            Message<UserAddBonusMsgDTO> message = MessageBuilder
                    // MQ 消息体
                    .withPayload(addBonusMsgDTO)
                    // 设置Header | + 事务id | + share_id
                    .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionalID)
                    .setHeader("share_id", id)
                    .build();

            this.rocketMQTemplate.sendMessageInTransaction(
                    "txBonusGroup",
                    "add-bonus",
                    message,
                    shareAuditDTO);
        } else {
            // 审核拒绝 不加积分
            this.auditByIdInDB(id, shareAuditDTO);
        }


        // 2.修改状态为通过或拒绝
//        auditByIdInDB(shareAuditDTO, share);

        // 3.如果通过 则为发布人增加积分 (需调其他微服务的api)
        //   userCenterFeignClient.addBouns(id,500)

        // 当主业务执行完成后，需要执行附属业务，但附属业务比较耗时，我们应当考虑将附属业务
        // 设为异步执行，不关心附属业务的响应，简短响应时长
        // 异步是具体实现：
        // - AsyncRestTemplate
        // - @Async 推荐
        // - WebClient spring 5.0
        // - MQ

        // 发送消息给MQ topic为add-bonus
//        this.rocketMQTemplate.convertAndSend("add-bonus", addBonusMsgDTO);

        // TODO 针对事务问题 ，如果这里抛出异常，则本地事务回滚，MQ已发送成功，消费端则未回滚
        // 方案：两次提交机制
        // - 1. 发送 半消息 到 MQ server，该消息是特殊类型消息，不能被消费
        // - 2. 本地事务执行后，成功发送 commit到 MQ server，失败发送 rollback到 MQ server
        // - 3.1 MQ server 接收到 commit，推送事务消息到消费端，消费端执行事务
        // - 3.2 MQ server 接收到 rollback，删除掉半消息
        // - 4. 有半消息长时间没有接收到 commit 或者 rollback，MQ server询问提供者该事务的执行情况，提供者再发送 commit 或 rollback到MQ server

//        int i = 1 / 0;


        return share;
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO shareAuditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(shareAuditDTO.getAuditStatusEnum().toString())
                .reason(shareAuditDTO.getReason())
                .build();

        this.shareMapper.updateByPrimaryKeySelective(share);
    }

    // 执行本地事务并 保存日志
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO shareAuditDTO, String transactionalID) {
        this.auditByIdInDB(id, shareAuditDTO);

        RocketMqTransaction rocketMqTransaction = RocketMqTransaction.builder()
                .transactionId(transactionalID)
                .log("审核内容")
                .createTime(new Date())
                .build();

        this.rocketMqTransactionMapper.insert(rocketMqTransaction);
    }
}
