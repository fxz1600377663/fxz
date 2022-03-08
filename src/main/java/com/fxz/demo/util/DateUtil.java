package com.fxz.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 */
public class DateUtil {
    public static String getNowDateString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        return simpleDateFormat.format(new Date());
    }
}
