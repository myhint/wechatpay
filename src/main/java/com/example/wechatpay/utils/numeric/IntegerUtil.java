package com.example.wechatpay.utils.numeric;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Blake on 2018/7/18
 */
public class IntegerUtil {

    public static boolean isZero(Integer value) {
        if (toInt(value) == 0) {
            return true;
        }
        return false;
    }

    public static int toInt(Integer value) {
        if (value == null) {
            return 0;
        }
        return value;
    }

    public static Integer toInteger(Integer value) {
        if (value == null) {
            return 0;
        }
        return value;
    }

    public static Integer toInteger(String value) {
        if (value == null) {
            return null;
        }
        return toInt(value);
    }

    public static int toInt(String value) {
        if (value == null || "".equals(value)) {
            return 0;
        }
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int toInt(Byte value) {
        if (value == null) {
            return 0;
        }
        try {
            return value.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isNotZero(Integer value) {
        return !isZero(value);
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    public static boolean isNumericForAmount(String str) {
        //Pattern pattern = Pattern.compile("^([0-9]*)+(.[0-9]{1,2})?$");
        //Pattern pattern = Pattern.compile("^([1-9][0-9]*)+(.[0-9]{1,2})?$");
        Pattern pattern = Pattern.compile("^(([1-9][0-9]*)|(([0]\\.\\d{1,2}|[1-9][0-9]*\\.\\d{1,2})))$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return boolean
     */
    public static boolean isNumer(String str) {
        Pattern pattern = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为n位小数数字
     *
     * @param str
     * @return boolean
     */
    public static boolean isNumerScaleTwo(String str) {
        Pattern pattern = Pattern.compile("^(\\-)?\\d+(\\.\\d{1,2})?$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 将单位为元转换为单位为分
     *
     * @param yuan 将要转换的元的数值字符串
     */
    public static Integer yuanToFee(String yuan) {
        return new BigDecimal(yuan).setScale(2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100)).intValue();
    }

    public static void main(String[] args) {

        String str = "000000000.00000000000";
        System.out.println(IntegerUtil.isNumer(str));
        System.out.println(IntegerUtil.isNumerScaleTwo(str));

    }
}
