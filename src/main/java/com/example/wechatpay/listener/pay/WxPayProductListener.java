package com.example.wechatpay.listener.pay;

import com.example.wechatpay.event.pay.WxPayProductEvent;
import com.example.wechatpay.service.app.order.AppOrderService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Description 商品订单使用微信支付的事件监听器
 * @Author blake
 * @Date 2018-12-17 14:55
 * @Version 1.0
 */
@Component
public class WxPayProductListener implements ApplicationListener<WxPayProductEvent> {

    private static final Logger logger = LoggerFactory.getLogger(WxPayProductListener.class);

    @Autowired
    private AppOrderService appOrderService;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    @Async
    /** 开启异步 */
    public void onApplicationEvent(WxPayProductEvent event) {

        logger.info("WxPayProductListener.onApplicationEvent ========= 商品订单结算事件处理 start ========= ");

        // 订单号
        String orderSn = event.getOrderSn();
        String transactionId = event.getTransactionId();

        // 从redis中取出数据
        RBucket<Object> bucket = redissonClient.getBucket(orderSn);

        // 微信支付 付款成功态
        if (event.getPayStatus()) {

            // TODO 1.从bucket中取出订单相关数据项

            // TODO 2.判断是否已处理，此举可有效防止重复处理
            if (appOrderService.hasProcessed(orderSn)) {
                logger.info("WxPayProductListener.onApplicationEvent ========= 商品订单结算事件已处理过 ========= ");
                return;
            }

            // TODO 3.支付成功后，商品对于用户应该是可见状态（update操作）

            // TODO 4.修改订单状态为"已完成"

            // 移除
            bucket.getAndDelete();
        } else {
            // 支付失败
            logger.info("WxPayProductListener.onApplicationEvent ========= 商品订单结算事件处理 暂无 ========= ");
        }
    }

}
