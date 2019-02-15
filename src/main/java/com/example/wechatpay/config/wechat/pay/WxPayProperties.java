package com.example.wechatpay.config.wechat.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description 微信支付配置项信息
 * @Author blake
 * @Date 2018/12/11 下午4:22
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "wxpay")
public class WxPayProperties {

    // 小程序唯一标识
    private String appid;

    // 微信收款商户号
    private String mchId;

    // 微信支付开发者密钥=>用于生成签名
    private String mchKey;

    // 支付回调url
    private String notifyUrl;

    // 退款申请url
    private String refundUrl;

    // 统一下单url
    private String unifiedOrderUrl;

    // 退款回调url
    private String refundNotifyUrl;

    // 退款证书路径
    private String certPath;

    // 微信支付调试开关
    private Boolean simulate;

    // 退款申请（沙箱环境）
    private String sandboxRefundUrl;

}
