package com.fanyao.alibaba.contentcenter.domain.entity.rocketmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rocketmq_transaction")
// 事务日志
public class RocketMqTransaction {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "log")
    private String log;

    @Column(name = "create_time")
    private Date createTime;
}