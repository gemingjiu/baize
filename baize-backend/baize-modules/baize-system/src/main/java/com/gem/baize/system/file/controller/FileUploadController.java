package com.gem.baize.system.file.controller;

import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.file.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/system/file")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public ApiResult<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = fileUploadService.upload(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("name", file.getOriginalFilename());
        return ApiResult.ok(result);
    }

    @PostMapping("/upload/multiple")
    @Operation(summary = "多文件上传")
    public ApiResult<Map<String, Object>> uploadMultiple(@RequestParam("files") MultipartFile[] files) throws IOException {
        Map<String, Object> result = new HashMap<>();
        java.util.List<Map<String, String>> list = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            String url = fileUploadService.upload(file);
            Map<String, String> item = new HashMap<>();
            item.put("url", url);
            item.put("name", file.getOriginalFilename());
            list.add(item);
        }
        result.put("files", list);
        return ApiResult.ok(result);
    }
}
