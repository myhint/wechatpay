package com.example.wechatpay.dto.order.product;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 商品订单信息
 * @Author blake
 * @Date 2019-02-15 15:32
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class ProductOrderDTO {

    // 商品id
    private Long productId;

    // 商品数量
    private Integer quantity;

    // 商品售价
    private Double price;

}
