package com.example.wechatpay.listener.refund;

import com.example.wechatpay.dto.order.items.OrderItemsDTO;
import com.example.wechatpay.event.refund.WxRefundProdEvent;
import com.example.wechatpay.service.app.order.AppOrderService;
import com.example.wechatpay.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Description 已下单支付成功商品的微信退款事件监听器
 * @Author blake
 * @Date 2018-12-27 11:13
 * @Version 1.0
 */
@Component
public class WxRefundProdListener implements ApplicationListener<WxRefundProdEvent> {

    private static final Logger logger = LoggerFactory.getLogger(WxRefundProdListener.class);

    @Autowired
    private AppOrderService appOrderService;

    @Override
    @Async
    public void onApplicationEvent(WxRefundProdEvent event) {

        if (event.getRefundStatus()) {

            logger.info("WxRefundProdListener.onApplicationEvent ======= 退款事件的监听处理START 退款通知状态为SUCCESS ======= ");

            String orderSn = event.getOrderSn();
            String transactionId = event.getTransactionId();

            // 获取订单明细项
            OrderItemsDTO orderItemsDTO = appOrderService.getOrderItem(orderSn, transactionId);

            if (Objects.isNull(orderItemsDTO)) {
                logger.info("WxRefundProdListener.onApplicationEvent ======= 退款事件的监听处理，订单明细项数据为空 ======= ");
                return;
            }

            // 判断订单类型：1=课程；2=礼品卡
            switch (orderItemsDTO.getProdType()) {

                case Constants.OrderProductType.PROD_COURSE:

                    // TODO 1.退款牵扯的相关逻辑处理
                    break;
                case Constants.OrderProductType.PROD_GIFT_CARD:

                    // TODO 2.退款牵扯的相关逻辑处理
                    break;
            }

        } else {
            // 退款异常
            logger.info("WxRefundProdListener.onApplicationEvent ========= 微信退款事件处理 暂无 ========= ");
        }

    }

}
