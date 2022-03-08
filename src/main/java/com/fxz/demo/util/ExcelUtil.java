package com.fxz.demo.util;

import com.fxz.demo.model.Student;
import com.fxz.demo.util.sax.ExcelReadDataDelegated;
import com.fxz.demo.util.sax.ExcelXlsxReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Index;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.fxz.demo.constant.ExcelConstant.EXCEL07_EXTENSION;
import static com.fxz.demo.constant.ExcelConstant.PER_SHEET_ROW_COUNT;

/**
 * excel工具类
 */
@Slf4j
public class ExcelUtil {

    private static ExcelUtil instance = new ExcelUtil();

    private ExcelUtil() {

    }

    public static ExcelUtil getInstance() {
        return instance;
    }

    /**
     * 生成workbook对象
     * @return SXSSFWorkbook
     */
    public static SXSSFWorkbook getWorkBook() {
        List<Student> students = new ArrayList<>();
        for (int i = 0;i < 1000000; i++) {
            students.add(new Student(i+1, "fxz"+i, new Random().nextInt(100), "男"));
        }
        List<Map<String, Object>> mapList = students.parallelStream().map(model ->
        {
            Map<String,Object> map = new HashMap<>();
            map.put("name", model.getName());
            map.put("age", model.getAge());
            map.put("tSex", model.getTSex());
            return map;
        }).collect(Collectors.toList());
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        // 临时文件会被压缩temp files will be gzipped
        workbook.setCompressTempFiles(true);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        String[] titles = {"名字", "年龄", "性别"};
        String[] columns = {"name", "age", "tSex"};
        int rowIndex = 1;
        int sheetIndex = 1;
        SXSSFSheet sheet = null;
        for (Map<String,Object> rowMap: mapList) {
            int currentIndex = mapList.indexOf(rowMap);
            SXSSFRow row;
            if (currentIndex % PER_SHEET_ROW_COUNT == 0) {
                sheet = workbook.createSheet("sheet_" + sheetIndex++);
                row = sheet.createRow(0);
                // 设置excel列名称
                for (int i = 0; i < titles.length; i++) {
                    SXSSFCell cell = row.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellType(CellType.STRING);
                    cell.setCellStyle(cellStyle);
                }
                rowIndex = 1;
            }
            if (sheet == null) {
                log.error("sheet is null");
            }
            row = sheet.createRow(rowIndex++);
            // 设置excel表格内容
            for (int i = 0; i < columns.length; i++) {
                row.createCell(i).setCellValue(String.valueOf(rowMap.get(columns[i])));
            }
            log.info(String.valueOf(rowIndex));
        }
        return workbook;
    }


    /**
     * 写入excel数据到response
     * @param workbook
     * @param excelName
     * @param response
     * @throws IOException
     */
    public static void writeExcel(SXSSFWorkbook workbook, String excelName, HttpServletResponse response) throws IOException {
        OutputStream outputStream = response.getOutputStream();
        try {
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelName, "UTF-8"));
            response.addHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET");
            workbook.write(outputStream);
        }finally {
            workbook.close();
            outputStream.close();
        }
    }

    /**
     * 读取excel表格数据
     * @param filePath path
     * @param excelReadDataDelegated 接口
     * @throws Exception
     */
    public static int readExcel(String filePath, ExcelReadDataDelegated excelReadDataDelegated) throws Exception {
        int totalRows = 0;
        if (filePath.endsWith(EXCEL07_EXTENSION)) {
            ExcelXlsxReader excelXlsxReader = new ExcelXlsxReader(excelReadDataDelegated);
            totalRows = excelXlsxReader.process(filePath);
        } else {
            throw new Exception("文件格式错误，fileName的扩展名只能是xlsx!");
        }
        System.out.println("读取的数据总行数：" + totalRows);
        return totalRows;
    }

    /**
     * 测试读取本地excel
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        String path = "C:\\Users\\18672\\Desktop\\WorkSpace\\students_20220306203034.xlsx";
        ExcelUtil.readExcel(path, (sheetIndex, totalRowCount, curRow, cellList) ->
                System.out.println("总行数为：" + totalRowCount + " 行号为：" + curRow + " 数据：" + cellList));
    }


    /**
     * 定义excel表格名称
     * @param excelName excelName
     * @return excelName
     */
    public static String getExcelName(String excelName) {
        String currentDate = DateUtil.getNowDateString();
        return String.format(Locale.ENGLISH, "%s_%s.xlsx", excelName, currentDate);
    }


}
