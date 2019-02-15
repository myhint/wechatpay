package com.example.wechatpay.service.common.wxpay.impl;

import com.example.wechatpay.config.wechat.pay.WxPayProperties;
import com.example.wechatpay.event.pay.WxPayProductEvent;
import com.example.wechatpay.exception.CommonBusinessException;
import com.example.wechatpay.service.app.order.AppOrderService;
import com.example.wechatpay.service.common.wxpay.WxPayService;
import com.example.wechatpay.utils.wxpay.WxPayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
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

    @Autowired
    private AppOrderService appOrderService;

    @Autowired
    private ApplicationContext applicationContext;


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

    /**
     * @return void
     * @throws
     * @description 微信支付异步通知
     * @params [request, response]
     */
    @Override
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 响应给微信的Xml格式数据
        String resXml = "";

        logger.info("WxPayServiceImpl.payNotify ========= 微信支付异步回调START ========= ");

        // 预先设定返回的 response 类型为 xml
        response.setHeader("Content-type", "application/xml");

        // 读取参数，解析Xml为map
        Map<String, String> map = wxPayUtils.transferXmlToMap(wxPayUtils.readRequest(request));

        // 转换为有序 map，校验签名是否正确
        boolean isSignSuccess = wxPayUtils.checkSign(new TreeMap<>(map), wxPayProperties.getMchKey());

        if (isSignSuccess) {
            // 签名校验成功，说明是微信服务器发出的数据
            String orderSn = map.get("out_trade_no");
            String transactionId = map.get("transaction_id");

            logger.info("WxPayServiceImpl.payNotify ============ 订单支付异步回调成功，订单号：[{}]，外部订单号：[{}] " +
                    "============ ", orderSn, transactionId);

            // 判断该订单是否已经被接收处理过
            if (appOrderService.hasProcessed(orderSn)) {
                resXml = checkSuccess();
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
                return;
            }

            if (map.get("return_code").equals("SUCCESS")) {
                // 支付成功
                if (map.get("result_code").equals("SUCCESS")) {

                    // 发布事件=>支付成功
                    applicationContext.publishEvent(new WxPayProductEvent(new Object(), orderSn, true, transactionId));

                    logger.info("WxPayServiceImpl.payNotify ============ 订单支付成功，事件发布成功，此次订单号：[{}] " +
                            "============ ", orderSn);

                    // 修改订单状态
                    appOrderService.completeOrder(orderSn, transactionId);

                } else {
                    // 支付失败 => 修改订单状态为"交易异常"
                    appOrderService.failOrder(orderSn);
                }
            }

            resXml = checkSuccess();
        } else {
            // 签名校验失败（原因分析：可能不是微信服务器发出的数据或者其他）
            resXml = checkFail();
        }

        // 处理业务完毕
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
    }

    /**
     * @return void
     * @throws
     * @description 微信退款异步通知
     * @params [request, response]
     */
    @Override
    public void refundNotify(HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * @return java.lang.String
     * @throws
     * @description 通知校验成功
     * @params []
     */
    private String checkSuccess() {

        return "<xml>\n" +
                "\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
    }

    /**
     * @return java.lang.String
     * @throws
     * @description 通知校验失败
     * @params []
     */
    private String checkFail() {

        return "<xml>\n" +
                "\n" +
                "  <return_code><![CDATA[FAIL]]></return_code>\n" +
                "  <return_msg><![CDATA[]]></return_msg>\n" +
                "</xml>";
    }

}
