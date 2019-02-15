package com.example.wechatpay.response.common.wxpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 微信支付必备参数
 * @Author blake
 * @Date 2018-12-17 22:21
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class WxPayArgsResponse {

    private String signType;

    private String appId;

    private String nonceStr;

    @JsonProperty(value = "package")
    private String prepayId;

    private String timeStamp;

    private String paySign;
}
