package com.example.wechatpay.response.app;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description App 用户信息
 * @Author blake
 * @Date 2019-02-15 15:37
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class AppUserResponse {

    // 用户id
    private Long id;

    // 微信用户唯一 openid
    private String openId;

}
