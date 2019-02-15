package com.example.wechatpay.service.common.wxpay;

import java.io.IOException;
import java.util.Map;

public interface WxPayService {

    /**
     * 组装微信支付API必需数据项
     */
    Map<String, Object> createPrepaidOrder(String orderSn, int totalFee, String body, String ipAddress, String openId)
            throws IOException;

}
