package com.fxz.demo.util;

import com.fxz.demo.model.Student;
import com.fxz.demo.util.sax.ExcelReadDataDelegated;
import com.fxz.demo.util.sax.ExcelXlsxReader;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static com.fxz.demo.constant.ExcelConstant.EXCEL07_EXTENSION;
import static com.fxz.demo.constant.ExcelConstant.PER_SHEET_ROW_COUNT;
import static java.util.Objects.isNull;

/**
 * excel工具类
 */
@Slf4j
public class ExcelUtil {
    public static final Random RANDOM = new Random();
    private ExcelUtil() {
    }

    /**
     * 生成workbook对象
     *
     * @return SXSSFWorkbook
     */
    public static SXSSFWorkbook getWorkBook() {
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        // 临时文件会被压缩temp files will be gzipped
        workbook.setCompressTempFiles(true);

        final int EXPORT_TOTAL_NUM = 1000000;
        List<FutureTask<Boolean>> exportFutures = new ArrayList<>();
        for (int index = 0, sheetNum = 1; index < EXPORT_TOTAL_NUM; ++sheetNum) {
            final int startDataIndex = index;
            final int endDataIndex = Math.min(EXPORT_TOTAL_NUM, startDataIndex + PER_SHEET_ROW_COUNT);

            // 创建任务
            final String sheetName = "fxz_" + sheetNum;
            Callable<Boolean> sheetTask = newSheetTask(workbook, sheetName, startDataIndex, endDataIndex);
            FutureTask<Boolean> future = new SheetTaskFuture<>(sheetName, sheetTask);
            exportFutures.add(future);
            index = endDataIndex;
        }

        // 执行任务
        for (FutureTask<Boolean> task : exportFutures) {
            new Thread(task).start();
        }

        // 等待任务结束
        for (Future<Boolean> future : exportFutures) {
            try {
                future.get();
            } catch (Exception ignored) {
                // TODO do nothing
            }
        }

        return workbook;
    }

    private static Callable<Boolean> newSheetTask(
            SXSSFWorkbook workbook, String sheetName, int startIndex, int endIndex) {
        // 创建表单
        final SXSSFSheet sheet = workbook.createSheet(sheetName);
        Objects.requireNonNull(sheet, "create sheet failed");

        // 写入表头
        String[] titles = {"名字", "年龄", "性别"};
        final int nextRow = writeRow(sheet, 0, titles, getCellStyle(workbook));

        return () -> {
            try {
                int rowIndex = nextRow;
                // 写入批量学生数据
                for (int index = startIndex; index < endIndex; ++index) {
                    Student student = Student.builder().id(index).name("fxz" + index)
                            .age(RANDOM.nextInt(100)).tSex("男").build();
                    Object[] attrArr = student.toAttrArr();
                    rowIndex = writeRow(sheet, rowIndex, attrArr, null);
                }
                return true;
            } catch (Exception exp) {
                log.error("sheet [{}] export exception: [{}]", sheetName, exp.getMessage());
            }
            return false;
        };
    }

    private static int writeRow(SXSSFSheet sheet, int rowIndex, Object[] columns, CellStyle style) {
        if (isNull(columns) || columns.length <= 0) {
            return rowIndex;
        }

        SXSSFRow row = sheet.createRow(rowIndex);
        for (int index = 0; index < columns.length; index++) {
            SXSSFCell cell = row.createCell(index);
            cell.setCellValue(Optional.ofNullable(columns[index]).map(String::valueOf).orElse(""));
            if (!isNull(style)) {
                cell.setCellStyle(style);
            }
        }
        return ++rowIndex;
    }

    private static CellStyle getCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private static class SheetTaskFuture<T> extends FutureTask<T> {
        private final String sheetName;

        public SheetTaskFuture(String sheetName, Callable<T> callable) {
            super(callable);
            this.sheetName = sheetName;
        }

        public SheetTaskFuture(String sheetName, Runnable runnable, T result) {
            super(runnable, result);
            this.sheetName = sheetName;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            try {
                T t = super.get();
                log.info("sheet task [{}] result: [{}]", sheetName, t);
                return t;
            } catch (Exception exp) {
                log.error("sheet export task [{}] get result exception: [{}]", sheetName, exp.getMessage());
            }
            return null;
        }
    }

    /**
     * 写入excel数据到response
     *
     * @param workbook  excel工作簿
     * @param excelName excel名称
     * @param response  响应
     * @throws IOException
     */
    public static void writeExcel(SXSSFWorkbook workbook, String excelName, HttpServletResponse response)
            throws IOException {
        OutputStream outputStream = response.getOutputStream();
        try {
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelName, "UTF-8"));
            response.addHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET");
            workbook.write(outputStream);
        } finally {
            workbook.close();
            outputStream.close();
        }
    }

    /**
     * 读取excel表格数据
     *
     * @param filePath               path
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
     *
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
     *
     * @param excelName excelName
     * @return excelName
     */
    public static String getExcelName(String excelName) {
        String currentDate = DateUtil.getNowDateString();
        return String.format(Locale.ENGLISH, "%s_%s.xlsx", excelName, currentDate);
    }


}
