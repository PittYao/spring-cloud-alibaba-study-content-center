package com.fanyao.alibaba.contentcenter.rocketmq;

import com.fanyao.alibaba.contentcenter.dao.rocketmq.RocketMqTransactionMapper;
import com.fanyao.alibaba.contentcenter.domain.dto.share.ShareAuditDTO;
import com.fanyao.alibaba.contentcenter.domain.entity.rocketmq.RocketMqTransaction;
import com.fanyao.alibaba.contentcenter.service.ShareService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * @author: bugProvider
 * @date: 2020/3/19 13:18
 * @description: 分布式事务二次提交控制
 */
@Data
@RocketMQTransactionListener(txProducerGroup = "txBonusGroup") // txProducerGroup要和提交半消息时的 group一致
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddBonusTransactionListener implements RocketMQLocalTransactionListener {

    private final ShareService shareService;
    private final RocketMqTransactionMapper rocketMqTransactionMapper;

    // 参数说明: sendMessageInTransaction中的参数
    // 执行本地事务
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // 从消息体中获取信息
        MessageHeaders headers = msg.getHeaders();
        String transactionalID = headers.get(RocketMQHeaders.TRANSACTION_ID, String.class);
        String shareId = headers.get("share_id",String.class);
        Integer shareIdInt = Integer.valueOf(shareId);

        try {
            // 执行事务 并保存 事务日志
//            shareService.auditByIdInDB(shareId, (ShareAuditDTO) arg);
            shareService.auditByIdWithRocketMqLog(shareIdInt, (ShareAuditDTO) arg, transactionalID);

            // TODO 可能的问题: 在这里 执行成功后 COMMIT未提交成功
            // 方案： MQ 长时间未接受到 RocketMQLocalTransactionState 会执行checkLocalTransaction
            // 执行成功后 存储本次事务信息到DB中，用于check查询事务执行状态
            // 成功 二次提交为 commit
            return RocketMQLocalTransactionState.UNKNOWN;
        } catch (Exception e) {
            // 失败 二次提交为 rollback
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    // mq server 没有接收到二次提交时 ，mq server 会调用该方法来查询事务的执行情况
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        MessageHeaders headers = msg.getHeaders();
        String transactionalID = headers.get(RocketMQHeaders.TRANSACTION_ID, String.class);
        // 查询状态
        RocketMqTransaction rocketMqTransaction = this.rocketMqTransactionMapper.selectOne(
                RocketMqTransaction.builder().transactionId(transactionalID).build()
        );

        if (rocketMqTransaction != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
