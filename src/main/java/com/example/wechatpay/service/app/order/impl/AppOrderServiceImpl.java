package com.example.wechatpay.service.app.order.impl;

import com.example.wechatpay.dto.order.items.OrderItemsDTO;
import com.example.wechatpay.dto.order.product.ProductOrderDTO;
import com.example.wechatpay.response.app.AppUserResponse;
import com.example.wechatpay.response.common.wxpay.WxPayArgsResponse;
import com.example.wechatpay.service.app.order.AppOrderService;
import com.example.wechatpay.service.common.wxpay.WxPayService;
import com.example.wechatpay.utils.Constants;
import com.example.wechatpay.utils.datetime.DateUtil;
import com.example.wechatpay.utils.jackson.JacksonUtil;
import com.example.wechatpay.utils.numeric.IntegerUtil;
import com.example.wechatpay.utils.random.RandomUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description App 订单业务逻辑层
 * @Author blake
 * @Date 2019-02-15 15:26
 * @Version 1.0
 */
@Service
public class AppOrderServiceImpl implements AppOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AppOrderServiceImpl.class);

    @Autowired
    private WxPayService wxPayService;

    /**
     * @return com.example.wechatpay.response.common.wxpay.WxPayArgsResponse
     * @throws
     * @description 商品订单&支付
     * 当前方法旨在：
     * 1）保存订单信息
     * 2）商品预归属于用户
     * 3）组装客户端正式调起微信支付API所需要的数据项
     * 4）微信异步回调有关业务处理需要数据项的提前部署
     * @params [orderJsonInfo, request, user]
     */
    @Override
    public WxPayArgsResponse orderProduct(String orderJsonInfo, HttpServletRequest request, AppUserResponse user) throws IOException {

        // 用户id（订单支付成功后，将与指定商品关联起来）
        Long userId = user.getId();

        // 微信用户唯一 openid（微信支付需要的配置数据项）
        String openId = user.getOpenId();

        // 商品订单信息
        List<ProductOrderDTO> productOrderDTOS = JacksonUtil.toList(orderJsonInfo,
                new TypeReference<List<ProductOrderDTO>>() {
                });

        // TODO 1.计算订单总价
        // 订单总价
        double totalPrice = 0;

        for (ProductOrderDTO productOrder : productOrderDTOS) {
            if (Objects.isNull(productOrder)) continue;
            totalPrice += productOrder.getPrice();
        }

        // TODO 2.生成唯一订单号
        String datetime = DateUtil.dateFormatWithyyyyMMddHHmmss(new Date());
        String fixLengthString = RandomUtils.getFixLengthString(14);
        // 订单编号（28位长度）
        String orderSn = datetime.concat(fixLengthString);

        // TODO 3.批量：保存订单条目
        // TODO 4.保存商品订单概况信息（此时的订单状态为：预下单）
        // TODO 5.商品与用户关联预热：往数据库插入记录，但此时商品状态对用户是不可见的（因为尚未支付成功）。

        // 3~5 自由灵活处理即可

        // TODO 6.支付场景描述：可理解为微信订单详情的标题，可自定义常量描述
        String body = Constants.UserOrderScenes.ORDER_SCENE_GIFT_CARD_PURCHASE;

        // TODO 7.调起微信支付API的准备工作：1）请求微信统一下单API; 2）再次签名
        WxPayArgsResponse wxPayArgsResponse = assembleWxPayArgs(orderSn, totalPrice, body, openId);

        // TODO 8.将订单相关数据存放至缓存中，如Redis，为微信异步回调接口的业务逻辑处理做准备

        // 第8步，应按实际业务需要，灵活处理

        return null;
    }

    /**
     * @return java.lang.Boolean
     * @throws
     * @description 判断订单是否已被处理过，防止重复处理
     * @params [orderSn]
     */
    @Override
    public Boolean hasProcessed(String orderSn) {

        return null;
    }

    /**
     * @return void
     * @throws
     * @description 订单已完成
     * @params [orderSn, transactionId]
     */
    @Override
    public void completeOrder(String orderSn, String transactionId) {

    }

    /**
     * @return void
     * @throws
     * @description 订单交易失败
     * @params [orderSn]
     */
    @Override
    public void failOrder(String orderSn) {

    }

    /**
     * @return com.example.wechatpay.dto.order.items.OrderItemsDTO
     * @throws
     * @description 获取订单明细项信息
     * @params [orderSn, transactionId]
     */
    @Override
    public OrderItemsDTO getOrderItem(String orderSn, String transactionId) {

        return null;
    }

    /**
     * @return WxPayArgsResponse
     * @throws
     * @description 组装微信支付API发起请求需要的数据项
     * @params [orderSn, totalPrice, body, openId]
     */
    private WxPayArgsResponse assembleWxPayArgs(String orderSn, double totalPrice, String body,
                                                String openId) throws IOException {

        // 客户端IP
        String ipAddress = "127.0.0.1";

        // 订单金额（元 => 分）
        int totalFee = IntegerUtil.yuanToFee(String.valueOf(totalPrice));

        // 1）请求微信统一下单API；2）再次签名为正式调起支付准备
        Map<String, Object> prepaidOrder = wxPayService.createPrepaidOrder(orderSn, totalFee, body, ipAddress, openId);

        WxPayArgsResponse wxPayArgsResponse = new WxPayArgsResponse();
        wxPayArgsResponse.setAppId((String) prepaidOrder.get("appId"));
        wxPayArgsResponse.setPaySign((String) prepaidOrder.get("paySign"));
        wxPayArgsResponse.setPrepayId((String) prepaidOrder.get("package"));
        wxPayArgsResponse.setSignType((String) prepaidOrder.get("signType"));
        wxPayArgsResponse.setNonceStr((String) prepaidOrder.get("nonceStr"));
        wxPayArgsResponse.setTimeStamp((String) prepaidOrder.get("timeStamp"));

        return wxPayArgsResponse;
    }

}
