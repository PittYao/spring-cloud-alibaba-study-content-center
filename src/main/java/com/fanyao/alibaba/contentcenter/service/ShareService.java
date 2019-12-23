package com.fanyao.alibaba.contentcenter.service;

import com.fanyao.alibaba.contentcenter.dao.share.ShareMapper;
import com.fanyao.alibaba.contentcenter.domain.dto.share.ShareDTO;
import com.fanyao.alibaba.contentcenter.domain.dto.user.UserDTO;
import com.fanyao.alibaba.contentcenter.domain.entity.share.Share;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
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
                .map(instance -> instance.getUri() + "/users/{id}")
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

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        // GET 调用
        UserDTO userDTO = restTemplate.getForObject(
                "http://127.0.0.1:8040/users/{id}",
                UserDTO.class,
                1
        );

        System.out.println(userDTO);

        // POST 调用
    }
}
