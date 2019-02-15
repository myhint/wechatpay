package com.example.wechatpay.config.wechat.pay;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description 微信支付配置类
 * @Author blake
 * @Date 2018/12/11 下午4:48
 * @Version 1.0
 */
@Configuration
public class WxPayConfig {

    @Bean
    @ConditionalOnMissingBean
    public WxPayProperties wxPayProperties() {

        return new WxPayProperties();
    }

}
