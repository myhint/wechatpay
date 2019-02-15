package com.example.wechatpay.service.common.wxpay.impl;

import com.example.wechatpay.config.wechat.pay.WxPayProperties;
import com.example.wechatpay.event.pay.WxPayProductEvent;
import com.example.wechatpay.event.refund.WxRefundProdEvent;
import com.example.wechatpay.exception.CommonBusinessException;
import com.example.wechatpay.service.app.order.AppOrderService;
import com.example.wechatpay.service.common.wxpay.WxPayService;
import com.example.wechatpay.utils.jackson.JacksonUtil;
import com.example.wechatpay.utils.wxpay.WxPayUtils;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
     * @return java.util.Map<java.lang.String   ,   java.lang.Object>
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

        logger.info("WxPayServiceImpl.refundNotify ========= 微信退款异步回调START ========= ");

        // 响应给微信的Xml格式数据
        String resXml = "";
        InputStream inStream;

        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            // 获取微信调用我们notify_url的返回信息
            String result = new String(outStream.toByteArray(), StandardCharsets.UTF_8);

            logger.info("WxPayServiceImpl.refundNotify ========== 微信退款异步通知信息：[{}] ========== ", result);

            // 关闭流
            outStream.close();
            inStream.close();

            // xml转换为map
            Map<String, String> map = wxPayUtils.transferXmlToMap(result);

            if ("SUCCESS".equalsIgnoreCase(map.get("return_code"))) {

                logger.info("WxPayServiceImpl.refundNotify ========== 微信退款通知成功返回 ========== ");

                /**
                 * 解密方式
                 * 解密步骤如下：
                 * （1）对加密串A做base64解码，得到加密串B
                 * （2）对商户key做md5，得到32位小写key* ( key设置路径：
                 *      微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置 )
                 * （3）用key*对加密串B做AES-256-ECB解密（PKCS7Padding）
                 */

                WxPayRefundNotifyResult refundResult = WxPayRefundNotifyResult.fromXML(result, wxPayProperties.getMchKey());

                WxPayRefundNotifyResult.ReqInfo reqInfo = refundResult.getReqInfo();

                logger.info("WxPayServiceImpl.refundNotify ========== 微信退款通知成功返回reqInfoStr：[{}] ========== ", JacksonUtil.toJSon(reqInfo));

                // 商户退款单号
                String outRefundNo = reqInfo.getOutRefundNo();
                // 退款状态
                String refundStatus = reqInfo.getRefundStatus();
                // 商户订单号
                String outTradeNo = reqInfo.getOutTradeNo();
                // 微信订单号
                String transactionId = reqInfo.getTransactionId();

                logger.info("WxPayServiceImpl.refundNotify =========== 商户退款订单号：[{}],商户付款单号：[{}]===========",
                        outRefundNo, outTradeNo);

                // 退款成功
                if ("SUCCESS".equals(refundStatus)) {
                    // 微信退款通知校验成功
                    resXml = checkSuccess();

                    // 根据付款单号判断付款记录是否存在
                    if (appOrderService.existsPayRecord(transactionId)) {

                        // 修改订单（支付订单和退订单）状态
                        appOrderService.switchRefundStatus(outRefundNo, outTradeNo);

                        // 发布退款成功事件通知
                        applicationContext.publishEvent(new WxRefundProdEvent(new Object(), transactionId, true, outTradeNo));

                    } else {
                        throw new CommonBusinessException("支付订单不存在或原订单已退款成功！");
                    }
                } else {
                    // 微信退款通知校验失败
                    resXml = checkFail();

                    // 根据付款单号判断付款记录是否存在
                    if (appOrderService.existsPayRecord(transactionId)) {
                        // 修改订单状态
                        appOrderService.switchRefundStatus(outRefundNo, outTradeNo);
                    } else {
                        throw new CommonBusinessException("支付订单不存在");
                    }
                }

            } else {
                logger.error("WxPayServiceImpl.refundNotify ========== 微信退款发生错误，错误原因：{} =========="
                        + map.get("return_msg"));

                resXml = checkFail();
            }
        } catch (Exception e) {
            logger.error("WxPayServiceImpl.refundNotify ========== refund:微信退款回调发生异常：{} ==========", e);
        } finally {
            try {
                // 处理业务完毕
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                logger.error("WxPayServiceImpl.refundNotify ========== refund:微信退款回调，处理数据流发生异常：{} ==========", e);
            }
        }

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
