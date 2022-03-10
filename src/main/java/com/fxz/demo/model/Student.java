package com.fxz.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class Student {
    private int id;
    private String name;
    private int age;
    private String tSex;

    /**
     * 转换成对象数组
     *
     * @return 对象数组
     */
    public Object[] toAttrArr() {
        Object[] arr = new Object[3];
        arr[0] = name;
        arr[1] = age;
        arr[2] = tSex;
        return arr;
    }
}
