package com.fxz.demo.service;

import com.fxz.demo.model.Student;
import com.fxz.demo.util.DateUtil;
import com.fxz.demo.util.ExcelUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


@Slf4j
@Service
public class ExcelService {

    @Value("${excel.upload.path}")
    private String tmpPath;

    @Autowired
    private StudentService studentService;

    public void exportExcel(HttpServletResponse response) throws IOException {
        String excelName = ExcelUtil.getExcelName("students");
        StopWatch watch = new StopWatch("export excel");
        watch.start(String.format(Locale.ROOT, "export excel [%s]", excelName));
        try (SXSSFWorkbook workbook = ExcelUtil.getWorkBook()){
            ExcelUtil.writeExcel(workbook, excelName, response);
        }
        watch.stop();
        log.info(watch.prettyPrint());
    }

    public void importExcel(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String tempFileName = DateUtil.getNowDateString() + fileName.substring(fileName.indexOf("."));
        File dest = new File(new File(tmpPath).getAbsoluteFile() + "/" + tempFileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        // excel临时文件
        file.transferTo(dest);
        // 存储excel表头
        HashMap<String, Integer> columnMap = new HashMap<>();
        List<Student> studentList = new ArrayList<>();
        try {
            int totalRows = ExcelUtil.readExcel(dest.getCanonicalPath(), (sheetIndex, totalRowCount, curRow, cellList) -> {
                // 获取excel表头
                if (curRow == 1) {
                    for (int i = 0;i < cellList.size(); i++) {
                        columnMap.put(cellList.get(i), i);
                    }
                    log.info("column header: {}", columnMap);
                } else {
                    Student student = new Student();
                    student.setName(cellList.get(columnMap.get("名字")));
                    student.setAge(Integer.parseInt(cellList.get(columnMap.get("年龄"))));
                    student.setTSex(cellList.get(columnMap.get("性别")));
                    studentList.add(student);
                    // 批量插入
                    if (studentList.size() > 0 && studentList.size() % 1000 == 0) {
                        studentService.batchInsertStudents(studentList);
                    }
                }
                System.out.println("总行数为：" + totalRowCount + " 行号为：" + curRow + " 数据：" + cellList);
            });
            if (studentList.size() > 0) {
                studentService.batchInsertStudents(studentList);
            }
            if (totalRows == 0) {
                throw new Exception("没有数据");
            }
        } finally {
            FileUtils.delete(dest);
        }
    }
}
