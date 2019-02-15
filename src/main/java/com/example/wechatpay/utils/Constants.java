package com.example.wechatpay.utils;

/**
 * @Description 全局常量
 * @Author blake
 * @Date 2019-02-15 16:39
 * @Version 1.0
 */
public class Constants {

    /**
     * 订单支付场景
     */
    public interface UserOrderScenes {

        String ORDER_SCENE_GIFT_CARD_PURCHASE = "礼品卡购买";

        String ORDER_SCENE_COURSE_PURCHASE = "课程购买";
    }

    /**
     * 订单商品类型
     */
    public interface OrderProductType {

        // 课程
        int PROD_COURSE = 1;

        // 礼品卡
        int PROD_GIFT_CARD = 2;

        // 蛋糕
        int PROD_CAKE = 3;
    }

}
