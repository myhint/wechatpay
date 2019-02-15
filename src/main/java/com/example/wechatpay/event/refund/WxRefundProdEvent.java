package com.example.wechatpay.event.refund;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @Description 微信退款事件源
 * @Author blake
 * @Date 2018-12-27 11:07
 * @Version 1.0
 */
@Data
public class WxRefundProdEvent extends ApplicationEvent {

    // 第三方订单号
    private String transactionId;

    // 退款状态
    private Boolean refundStatus;

    // 订单号
    private String orderSn;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public WxRefundProdEvent(Object source, String transactionId, Boolean refundStatus, String orderSn) {
        super(source);
        this.transactionId = transactionId;
        this.refundStatus = refundStatus;
        this.orderSn = orderSn;
    }

}
