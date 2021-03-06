package com.example.wechatpay.service.app.order;

import com.example.wechatpay.dto.order.items.OrderItemsDTO;
import com.example.wechatpay.response.app.AppOrderResponse;
import com.example.wechatpay.response.app.AppUserResponse;
import com.example.wechatpay.response.common.wxpay.WxPayArgsResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;

public interface AppOrderService {

    /**
     * 商品订单&支付
     */
    WxPayArgsResponse orderProduct(String orderJsonInfo, HttpServletRequest request, AppUserResponse user) throws IOException;

    /**
     * 判断订单是否已被处理过，防止重复处理
     */
    Boolean hasProcessed(String orderSn);

    /**
     * 订单已完成
     */
    void completeOrder(String orderSn, String transactionId);

    /**
     * 订单交易失败
     */
    void failOrder(String orderSn);

    /**
     * 获取订单明细项信息
     */
    OrderItemsDTO getOrderItem(String orderSn, String transactionId);

    /**
     * 判断付款记录是否存在
     */
    Boolean existsPayRecord(String transactionId);

    /**
     * 修改订单状态
     */
    void switchRefundStatus(String outRefundNo, String outTradeNo);

    /**
     * 获取订单总价
     */
    BigDecimal getTotalFee(String transactionId, Long orderId);

    /**
     * 获取订单信息
     */
    AppOrderResponse getOrderInfoByTransactionId(String transactionId);
}
