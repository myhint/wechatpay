package com.example.wechatpay.service.app.order;

import com.example.wechatpay.response.app.AppUserResponse;
import com.example.wechatpay.response.common.wxpay.WxPayArgsResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface AppOrderService {

    /**
     * 商品订单&支付
     */
    WxPayArgsResponse orderProduct(String orderJsonInfo, HttpServletRequest request, AppUserResponse user) throws IOException;
}
