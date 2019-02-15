package com.example.wechatpay.dto.order.items;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 订单明细项信息
 * @Author blake
 * @Date 2018-12-27 12:03
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class OrderItemsDTO {

    private Long id;

    private Long userId;

    private String orderSn;

    private String transactionId;

    private Long prodId;

    private Integer prodType;

}
