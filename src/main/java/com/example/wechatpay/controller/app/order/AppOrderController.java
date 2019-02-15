package com.example.wechatpay.controller.app.order;

import com.example.wechatpay.response.app.AppUserResponse;
import com.example.wechatpay.response.common.wxpay.WxPayArgsResponse;
import com.example.wechatpay.service.app.order.AppOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description App 订单控制层
 * @Author blake
 * @Date 2019-02-15 15:18
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/order")
public class AppOrderController {

    @Autowired
    private AppOrderService appOrderService;

    @PostMapping("/product/user")
    public WxPayArgsResponse orderProduct(@RequestParam String orderJsonInfo, HttpServletRequest request) {

        return appOrderService.orderProduct(orderJsonInfo, request, new AppUserResponse());
    }

}
