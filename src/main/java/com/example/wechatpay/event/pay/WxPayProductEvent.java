package com.example.wechatpay.event.pay;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @Description 微信支付商品事件源
 * @Author blake
 * @Date 2018-12-17 14:53
 * @Version 1.0
 */
@Data
public class WxPayProductEvent extends ApplicationEvent {

    // 订单号
    private String orderSn;

    // 支付状态
    private Boolean payStatus;

    // 第三方订单号
    private String transactionId;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public WxPayProductEvent(Object source, String orderSn, Boolean payStatus, String transactionId) {
        super(source);
        this.orderSn = orderSn;
        this.payStatus = payStatus;
        this.transactionId = transactionId;
    }

}
