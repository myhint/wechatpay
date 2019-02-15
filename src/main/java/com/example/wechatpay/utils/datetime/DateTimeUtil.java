package com.example.wechatpay.utils.datetime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Blake on 2018/8/8
 */
public class DateTimeUtil {

    /**
     * 按 yyyy-MM-dd HH:mm:ss 模式解析日期时间
     *
     * @param dateTime 建议调用此函数前，先对dateTime进行非空判断
     * @return
     */
    public static LocalDateTime parseDateTime(String dateTime) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(dateTime, dateTimeFormatter);
    }

    public static void main(String[] args) {

        LocalDateTime localDateTime = DateTimeUtil.parseDateTime("2018-08-08 16:30:33");

        System.out.println(localDateTime);
    }

}
