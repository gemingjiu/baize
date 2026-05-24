package com.gem.baize.system.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 上传路径
     */
    private String path = "/tmp/baize/upload";

    /**
     * 允许的最大文件大小（MB）
     */
    private long maxSize = 10;

    /**
     * 允许的文件类型
     */
    private String allowedTypes = "jpg,png,gif,bmp,doc,docx,xls,xlsx,pdf,txt,zip";
}
