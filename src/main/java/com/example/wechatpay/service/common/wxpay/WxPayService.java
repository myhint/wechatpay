package com.example.wechatpay.service.common.wxpay;

import com.example.wechatpay.request.common.WxRefundRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface WxPayService {

    /**
     * 组装微信支付API必需数据项
     */
    Map<String, Object> createPrepaidOrder(String orderSn, int totalFee, String body, String ipAddress, String openId)
            throws IOException;

    /**
     * 微信支付异步通知
     */
    void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 微信退款异步通知
     */
    void refundNotify(HttpServletRequest request, HttpServletResponse response);

    /**
     * 微信退款请求
     */
    Map refundOrder(WxRefundRequest refundRequest, HttpServletRequest request, HttpServletResponse response) throws IOException;

}
