package com.example.wechatpay.utils.numeric;


/**
 * Created by Blake on 2018/7/19
 */
public class LongUtil {

    public static boolean isZero(Long value) {
        if (toLong(value) == 0) {
            return true;
        }
        return false;
    }

    public static long toLong(Long value) {
        if (value == null) {
            return 0;
        }
        return value;
    }

    public static long toLong(Integer value) {
        if (value == null) {
            return 0L;
        }
        return new Long(value);
    }

    public static long toLong(Object value) {

        if (value == null || "".equals(value)) {
            return 0L;
        }
        try {
            return Long.valueOf(value.toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    public static long toLong(String value) {
        if (value == null || "".equals(value)) {
            return 0;
        }
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isNotZero(Long value) {
        return !isZero(value);
    }

}
