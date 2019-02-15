package com.example.wechatpay.service.common.wxpay.impl;

import com.example.wechatpay.config.wechat.pay.WxPayProperties;
import com.example.wechatpay.exception.CommonBusinessException;
import com.example.wechatpay.service.common.wxpay.WxPayService;
import com.example.wechatpay.utils.wxpay.WxPayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Description 微信支付 服务层
 * @Author blake
 * @Date 2019-02-15 16:45
 * @Version 1.0
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    private static final Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);

    @Autowired
    private WxPayUtils wxPayUtils;

    @Autowired
    private WxPayProperties wxPayProperties;


    /**
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @throws
     * @description 组装微信支付API必需数据项
     * @params [orderSn, totalFee, body, ipAddress, openId]
     */
    @Override
    public Map<String, Object> createPrepaidOrder(String orderSn, int totalFee, String body,
                                                  String ipAddress, String openId) throws IOException {

        if (StringUtils.isBlank(openId)) {
            logger.info("WxPayServiceImpl.createPrepaidOrder ======== 微信授权登录异常，openId为空 ======== ");
            throw new CommonBusinessException("微信授权登录状态异常，请检查后重试！");
        }

        SortedMap<String, Object> parameters = new TreeMap<>();

        parameters.put("appid", wxPayProperties.getAppid());
        parameters.put("mch_id", wxPayProperties.getMchId());
        // PC或公众号内支付统一传"WEB"
        parameters.put("device_info", "WEB");
        parameters.put("body", body);
        // 32 位随机字符串
        parameters.put("nonce_str", wxPayUtils.gen32RandomString());

        parameters.put("notify_url", wxPayProperties.getNotifyUrl());
        parameters.put("out_trade_no", orderSn);

        // parameters.put("total_fee", totalFee.multiply(BigDecimal.valueOf(100)).intValue());
        // 测试时，将支付金额设置为 1 分钱
        parameters.put("total_fee", totalFee);

        parameters.put("spbill_create_ip", ipAddress);

        // 公众号支付类型（小程序支付亦属于此范畴）
        parameters.put("trade_type", "JSAPI");

        // wx.login() 接口可获取到，也就意味着微信小程序成功登录后，即可获取到 openid
        parameters.put("openid", openId);

        // sign 必须在最后
        parameters.put("sign", wxPayUtils.createSign(parameters, wxPayProperties.getMchKey()));

        // 统一下单API=>沙箱环境
        // String placeUrl = "https://api.mch.weixin.qq.com/sandboxnew/pay/unifiedorder";

        // 统一下单API
        String placeUrl = wxPayProperties.getUnifiedOrderUrl();

        // 执行 HTTP 请求，获取接收的字符串（一段 XML）
        String res = wxPayUtils.executeHttpPost(placeUrl, parameters, null);

        logger.info("WxPayServiceImpl.createPrepaidOrder ======== 微信统一下单接口返回信息：{} ========", res);

        // 二次签名（小程序正式调起微信支付必填参数）
        return wxPayUtils.createSign2(res, wxPayProperties.getMchKey());
    }

}
