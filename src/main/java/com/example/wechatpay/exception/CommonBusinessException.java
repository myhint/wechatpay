package com.example.wechatpay.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by Roger on 2018/9/12
 */
public class CommonBusinessException extends RuntimeException {

    /**
     * 异常状态码
     */
    private int code = HttpStatus.BAD_REQUEST.value();

    /**
     * 异常描述信息
     */
    private String message;

    public CommonBusinessException() {
    }

    public CommonBusinessException(String message) {
        super(message);
        this.message = message;
    }

    public CommonBusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
