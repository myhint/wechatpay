package com.example.wechatpay.utils.random;

import java.util.Objects;
import java.util.Random;

/**
 * @Description 随机数工具类
 * @Author blake
 * @Date 2018/12/5 上午11:28
 * @Version 1.0
 */
public class RandomUtils {

    /**
     * 返回长度为[strLength]的随机数，在前面补0
     */
    public static String getFixLengthString(int strLength) {

        Random rm = new Random();

        // 获得随机数
        double randomSn = (1 + rm.nextDouble()) * Math.pow(10, strLength);

        // 将获得的获得随机数转化为字符串
        String randomSnString = String.valueOf(randomSn);

        // 返回固定的长度的随机数
        return randomSnString.substring(2, strLength + 2);
    }

    /**
     * 随机生成4位长度验证码
     */
    public static String getRandomCode() {
        final int max = 9999;
        final int min = 1;
        String randomCode = String.format("%04d", new Random().nextInt(max - min + 1) + min);

        // 递归调用：去掉以数字0开头的随机码
        if (Objects.nonNull(randomCode) && randomCode.startsWith("0")) {
            randomCode = getRandomCode();
        }

        return randomCode;
    }

    public static void main(String[] args) {

        for (int i = 0; i < 1000; i++) {

            System.out.println(getRandomCode());

        }
    }

}
