package com.fxz.demo;

import com.fxz.demo.model.Student;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelInsertDemo {
    private static final String EXCEL_PATH = "";


    public static void main(String[] args) {

        List<Integer> intList = new ArrayList<>();
        for (int i = 0;i < 100000;i++) {
            intList.add(i);
        }
        long start = System.currentTimeMillis();
        for (int i = 0;i < 100000;i++) {
            intList.indexOf(i);
            System.out.println("当前数值: " + i);
        }
        System.out.println("消耗时间 : " + (System.currentTimeMillis() - start)/1000 + " sec");

//        List<Student> students = new ArrayList<>();
//        for (int i = 0;i < 100000; i++) {
//            students.add(new Student(i+1, "fxz"+i, new Random().nextInt(100), "男"));
//        }
//        List<Map<String, Object>> mapList = students.parallelStream().map(model ->
//        {
//            Map<String,Object> map = new HashMap<>();
//            map.put("name", model.getName());
//            map.put("age", model.getAge());
//            map.put("tSex", model.getTSex());
//            return map;
//        }).collect(Collectors.toList());
//
//        long start = System.currentTimeMillis();
//        int index = 0;
//        for (Map<String, Object> rowMap: mapList) {
//            mapList.indexOf(rowMap);
//            System.out.println(index++);
//        }
//        System.out.println("消耗时间 : " + (System.currentTimeMillis() - start)/1000 + " sec");

    }
}
