package com.example.wechatpay.controller.app.wxpay;

import com.example.wechatpay.service.common.wxpay.WxPayService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 微信支付 控制层
 * @Author blake
 * @Date 2019-02-15 15:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/wechat")
public class AppWxPayController {

    @Autowired
    private WxPayService wxPayService;

    @ApiOperation(value = "微信支付异步通知")
    @PostMapping("/notify/async/pay")
    public void payNotify(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        wxPayService.payNotify(request, response);
    }

    @ApiOperation(value = "微信退款异步通知")
    @PostMapping("/notify/async/refund")
    public void refundNotify(HttpServletRequest request, HttpServletResponse response) {

        wxPayService.refundNotify(request, response);
    }

    @ApiOperation(value = "微信退款申请")
    @PostMapping("/refund")
    public void refundOrder() {


    }

}
