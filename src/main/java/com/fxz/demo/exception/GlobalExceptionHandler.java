package com.fxz.demo.exception;

import com.fxz.demo.constant.ErrorCode;
import com.fxz.demo.model.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.InsufficientResourcesException;
import java.io.FileNotFoundException;
import java.net.BindException;
import java.security.acl.NotOwnerException;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;
import java.util.MissingResourceException;
import java.util.jar.JarException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 铭感异常处理
     *
     * @return resultVo
     */
    @ExceptionHandler(value = {FileNotFoundException.class, JarException.class, MissingResourceException.class,
            NotOwnerException.class, ConcurrentModificationException.class, InsufficientResourcesException.class,
            BindException.class, OutOfMemoryError.class, StackOverflowError.class, SQLException.class})
    public ResultVo<Object> sensitiveErrorHandler() {
        log.error("Sensitive Error.");
        return ResultVo.error(ErrorCode.SERVICE_ERROR.getCode(), ErrorCode.SERVICE_ERROR.getMessagee());
    }
}
