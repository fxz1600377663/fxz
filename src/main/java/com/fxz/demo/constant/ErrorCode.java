package com.fxz.demo.constant;

/**
 * 异常状态码
 */
public enum ErrorCode {
    UNKNOWN_ERROR(9000, "unknown_error"),
    SERVICE_ERROR(9100, "unknown_error"),
    UNAUTHORIZED_ERROR(101, "unknown_error");

    int code;
    String messagee;

    ErrorCode(int code, String messagee) {
        this.code = code;
        this.messagee = messagee;
    }

    public int getCode() {
        return code;
    }

    public String getMessagee() {
        return messagee;
    }
}
