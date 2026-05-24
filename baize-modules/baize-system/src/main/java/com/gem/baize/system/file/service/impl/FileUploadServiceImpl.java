package com.gem.baize.system.file.service.impl;

import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.system.file.config.FileUploadConfig;
import com.gem.baize.system.file.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    public String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ParamException("文件不能为空");
        }

        // 校验文件大小
        long maxSize = fileUploadConfig.getMaxSize() * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new ParamException("文件大小超过限制: " + fileUploadConfig.getMaxSize() + "MB");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new ParamException("文件名不能为空");
        }
        String extension = getExtension(originalFilename).toLowerCase();
        String allowedTypes = fileUploadConfig.getAllowedTypes();
        if (allowedTypes != null && !allowedTypes.isEmpty()) {
            if (!allowedTypes.contains(extension)) {
                throw new ParamException("不支持的文件类型: " + extension);
            }
        }

        // 构建保存路径
        String uploadPath = fileUploadConfig.getPath();
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path targetPath = Paths.get(uploadPath, datePath, fileName);

        // 创建目录
        Files.createDirectories(targetPath.getParent());

        // 保存文件
        file.transferTo(targetPath.toFile());

        log.info("文件上传成功: {}", targetPath);
        return "/upload/" + datePath + "/" + fileName;
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex + 1) : "";
    }
}
