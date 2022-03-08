package com.fxz.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 请求返回消息实体类
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVo<T> {

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 0;

    /**
     * 失败状态码
     */
    public static final int ERROR_CODE = 1;

    public static final String SUCCESS_MESSAGE = "request is successful";

    public static final String ERROR_MESSAGE = "request is failed";

    private int resultCode;

    private String resultMessage;

    private T data;


    /**
     * 返回成功消息
     * @return ResultVo
     */
    public static <T> ResultVo<T> success() {
        return new ResultVo<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }


    /**
     * 返回成功消息
     * @param data 数据
     * @param <T> T
     * @return ResultVo
     */
    public static <T> ResultVo<T> success(T data) {
        return new ResultVo<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    /**
     * 返回失败消息
     * @param <T> T
     * @return ResultVo
     */
    public static <T> ResultVo<T> error() {
        return new ResultVo<>(ERROR_CODE, ERROR_MESSAGE, null);
    }

    /**
     * 返回失败消息
     * @param message 消息
     * @param <T> T
     * @return ResultVo
     */
    public static <T> ResultVo<T> error(String message) {
        return new ResultVo<>(ERROR_CODE, message, null);
    }

    /**
     * 返回失败消息
     * @param errorCode 错误状态码
     * @param message 消息
     * @param <T> T
     * @return ResultVo
     */
    public static <T> ResultVo<T> error(Integer errorCode, String message) {
        return new ResultVo<>(errorCode, message, null);
    }

}
