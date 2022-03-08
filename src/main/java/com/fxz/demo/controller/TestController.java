package com.fxz.demo.controller;

import com.fxz.demo.model.ResultVo;
import com.fxz.demo.model.Student;
import com.fxz.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private StudentService studentService;

    @GetMapping("hello")
    public ResultVo hello() {
        return ResultVo.success("hello");
    }

    @GetMapping("query")
    public ResultVo query(int id) {
        Student student = studentService.queryStudent(id);
        return ResultVo.success(student);
    }

    @PostMapping("insert")
    public ResultVo insert() {
        studentService.insertStudent();
        return ResultVo.success();
    }
}
