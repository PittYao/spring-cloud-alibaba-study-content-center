package com.fanyao.alibaba.contentcenter.service;

import com.fanyao.alibaba.contentcenter.dao.share.ShareMapper;
import com.fanyao.alibaba.contentcenter.domain.dto.share.ShareDTO;
import com.fanyao.alibaba.contentcenter.domain.dto.user.UserDTO;
import com.fanyao.alibaba.contentcenter.domain.entity.share.Share;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @author: bugProvider
 * @date: 2019/12/20 14:21
 * @description:
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {
    private final ShareMapper shareMapper;
    private final RestTemplate restTemplate;

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        // 发布人id
        Integer userId = share.getUserId();
        // 查询用户服务 发布人信息
        UserDTO userDTO= restTemplate.getForObject(
                "http://127.0.0.1:8040/users/{id}",
                UserDTO.class,
                userId
        );
        // 消息装配
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share,shareDTO);
        shareDTO.setWxNickname(Objects.requireNonNull(userDTO).getWxNickname());

        return shareDTO;
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        // GET 调用
        UserDTO userDTO= restTemplate.getForObject(
                "http://127.0.0.1:8040/users/{id}",
                UserDTO.class,
                1
        );

        System.out.println(userDTO);

        // POST 调用
    }
}
