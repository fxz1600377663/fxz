package com.fxz.demo.service;

import com.fxz.demo.dao.StudentMapper;
import com.fxz.demo.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    public Student queryStudent(int id) {
        return studentMapper.queryStudent(id);
    }

    public void insertStudent() {
        Student student = new Student();
        student.setName(UUID.randomUUID().toString().substring(0,5));
        student.setAge(new Random().nextInt(100));
        student.setTSex("男");
        studentMapper.insertStudent(student);
    }

    public void batchInsertStudents(List<Student> studentList) {
        studentMapper.batchInsertStudent(studentList);
        log.info("insert data row: " + studentList.size());
        studentList.clear();
    }

}
