package com.fxz.demo.dao;

import com.fxz.demo.model.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentMapper {

    void insertStudent(Student student);

    void batchInsertStudent(List<Student> studentList);

    Student queryStudent(int id);
}
