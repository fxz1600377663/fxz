package com.fxz.demo.controller;

import com.fxz.demo.model.ResultVo;
import com.fxz.demo.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * excel文件导入导出
 *
 */
@Slf4j
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;


    @PostMapping("upload")
    public ResultVo uploadExcel(MultipartFile file) throws Exception {
        log.info("start upload excel");
        excelService.importExcel(file);
        log.info("insert excel completed");
        return ResultVo.success();
    }


    @PostMapping("download")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        log.info("start export excel");
        excelService.exportExcel(response);
        log.info("export excel completed");
    }

}
