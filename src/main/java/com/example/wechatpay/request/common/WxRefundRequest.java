package com.example.wechatpay.request.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Description 微信退款 请求参数
 * @Author blake
 * @Date 2019-02-21 10:20
 * @Version 1.0
 */
@ApiModel
@Data
@NoArgsConstructor
public class WxRefundRequest {

    @ApiModelProperty(value = "微信订单号", position = 0)
    @NotBlank(message = "微信订单号不允许为空")
    private String transactionId;

    @ApiModelProperty(value = "退款金额", position = 2, hidden = true)
    private BigDecimal refundFee;

    @ApiModelProperty(value = "订单id", position = 1)
    @NotNull(message = "订单id不允许为空")
    @Min(value = 1, message = "订单id最小等于1")
    private Long orderId;

}
