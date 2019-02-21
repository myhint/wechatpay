package com.example.wechatpay.response.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description 订单信息
 * @Author blake
 * @Date 2019-02-21 10:29
 * @Version 1.0
 */
@ApiModel
@Data
@NoArgsConstructor
public class AppOrderResponse {

    @ApiModelProperty(value = "id", position = 0)
    private Long id;

    @ApiModelProperty(value = "用户id", position = 1)
    private Long userId;

    @ApiModelProperty(value = "订单号", position = 2)
    private String orderSn;

    @ApiModelProperty(value = "订单总价", position = 3)
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "外部支付订单号", position = 4)
    private String transactionId;

    @ApiModelProperty(value = "商品类型：1=课程，2=礼品卡", position = 5)
    private Integer prodType;
}
